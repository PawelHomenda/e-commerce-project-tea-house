import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { Product } from '../../models/product.model';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-product',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './product.html',
  styleUrl: './product.css',
})
export class ProductComponent implements OnInit, OnDestroy {
  product: Product | null = null;
  relatedProducts: Product[] = [];
  quantity = 1;
  isLoading = true;
  isAddingToCart = false;
  successMessage = '';
  errorMessage = '';
  
  private destroy$ = new Subject<void>();
  private productId: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private cartService: CartService
  ) {}

  ngOnInit(): void {
    this.route.paramMap
      .pipe(takeUntil(this.destroy$))
      .subscribe(params => {
        const id = params.get('id');
        if (id) {
          this.productId = parseInt(id, 10);
          this.loadProduct(this.productId);
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadProduct(id: number): void {
    this.isLoading = true;
    this.productService.getProductById(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (product) => {
          this.product = product;
          this.loadRelatedProducts(id);
          this.isLoading = false;
        },
        error: (error) => {
          this.errorMessage = 'Error al cargar el producto';
          this.isLoading = false;
          console.error('Error loading product:', error);
        }
      });
  }

  loadRelatedProducts(productId: number): void {
    this.productService.getActiveProducts()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (products: Product[]) => {
          this.relatedProducts = products.filter(p => p.id !== productId).slice(0, 4);
        },
        error: (error: any) => {
          console.error('Error loading related products:', error);
        }
      });
  }

  addToCart(): void {
    if (!this.product || this.quantity <= 0) {
      this.errorMessage = 'Cantidad inválida';
      return;
    }

    // Verificar stock
    if (this.quantity > this.product.stock) {
      this.errorMessage = `Stock insuficiente. Disponibles: ${this.product.stock}`;
      return;
    }

    this.isAddingToCart = true;
    this.cartService.addProduct(this.product.id, this.quantity, this.product)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = `${this.quantity} ${this.product!.name} añadido(s) al carrito`;
          this.quantity = 1;
          this.isAddingToCart = false;
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (error) => {
          this.errorMessage = error.error?.message || 'Error al añadir al carrito';
          this.isAddingToCart = false;
        }
      });
  }

  buyNow(): void {
    if (this.product && this.quantity > 0 && this.quantity <= this.product.stock) {
      // Primero añadir al carrito
      this.cartService.addProduct(this.product.id, this.quantity, this.product)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            // Redirigir al carrito
            this.router.navigate(['/cart']);
          },
          error: (error) => {
            this.errorMessage = 'Error al procesar la compra';
          }
        });
    }
  }

  viewProduct(productId: number): void {
    this.router.navigate(['/product', productId]);
  }

  decreaseQuantity(): void {
    if (this.quantity > 1) {
      this.quantity--;
    }
  }

  increaseQuantity(): void {
    if (this.product && this.quantity < this.product.stock) {
      this.quantity++;
    }
  }

  isOutOfStock(): boolean {
    return this.product ? this.product.stock === 0 : false;
  }

  getRatingPercentage(): number {
    return this.product ? (this.product.rating / 5) * 100 : 0;
  }

  Math = Math; // Exponer Math al template
}
