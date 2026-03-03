import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProductService } from '../../services/product.service';
import { OrderService } from '../../services/order.service';
import { AuthService } from '../../services/auth.service';
import { Product, ProductDTO, Category, PagedResponse } from '../../models/product.model';
import { Order, OrderStatus } from '../../models/order.model';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-admin',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './admin.html',
  styleUrl: './admin.css',
})
export class AdminComponent implements OnInit, OnDestroy {
  // Exponemos los tipos para el template
  OrderStatus = OrderStatus;

  // Tabs
  activeTab: 'products' | 'orders' = 'products';

  // Productos
  products: Product[] = [];
  categories: Category[] = [];
  productForm!: FormGroup;
  isEditingProduct = false;
  selectedProduct: Product | null = null;
  isLoadingProducts = true;
  isSubmittingProduct = false;
  currentProductPage = 0;
  totalProductPages = 0;

  // Órdenes
  orders: Order[] = [];
  orderStatusFilter: OrderStatus | '' = '';
  isLoadingOrders = true;
  currentOrderPage = 0;
  totalOrderPages = 0;

  // Mensajes
  successMessage = '';
  errorMessage = '';

  private destroy$ = new Subject<void>();

  constructor(
    private productService: ProductService,
    public orderService: OrderService,
    private authService: AuthService,
    private fb: FormBuilder
  ) {
    this.initializeProductForm();
  }

  ngOnInit(): void {
    // Verificar si es admin
    if (!this.authService.isAdmin()) {
      this.errorMessage = 'No tienes permisos de administrador';
      return;
    }

    this.loadCategories();
    this.loadProducts();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initializeProductForm(): void {
    this.productForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      price: ['', [Validators.required, Validators.min(0.01)]],
      stock: ['', [Validators.required, Validators.min(0)]],
      categoryId: ['', Validators.required],
      imageUrl: ['', Validators.required],
      thumbnailUrl: [''],
      origin: ['', Validators.required],
      weight: ['', [Validators.required, Validators.min(0.1)]],
      unit: ['g', Validators.required]
    });
  }

  // ==================== PRODUCTOS ====================

  loadCategories(): void {
    this.productService.getAllCategories()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (categories) => {
          this.categories = categories;
        },
        error: (error) => {
          console.error('Error loading categories:', error);
        }
      });
  }

  loadProducts(): void {
    this.isLoadingProducts = true;
    this.productService.getAllProducts({ page: this.currentProductPage, size: 10 })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response: PagedResponse<Product>) => {
          this.products = response.content;
          this.totalProductPages = response.totalPages;
          this.isLoadingProducts = false;
        },
        error: (error) => {
          this.errorMessage = 'Error al cargar productos';
          this.isLoadingProducts = false;
        }
      });
  }

  addProductForm(): void {
    this.isEditingProduct = true;
    this.selectedProduct = null;
    this.productForm.reset({ unit: 'g' });
  }

  editProduct(product: Product): void {
    this.isEditingProduct = true;
    this.selectedProduct = product;
    this.productForm.patchValue({
      name: product.name,
      description: product.description,
      price: product.price,
      stock: product.stock,
      categoryId: product.category.id,
      imageUrl: product.imageUrl,
      thumbnailUrl: product.thumbnailUrl,
      origin: product.origin,
      weight: product.weight,
      unit: product.unit
    });
  }

  submitProduct(): void {
    if (this.productForm.invalid) {
      this.errorMessage = 'Por favor completa todos los campos requeridos';
      return;
    }

    this.isSubmittingProduct = true;
    const productDTO: ProductDTO = this.productForm.value;

    const request = this.selectedProduct 
      ? this.productService.updateProduct(this.selectedProduct.id, productDTO)
      : this.productService.createProduct(productDTO);

    request.pipe(takeUntil(this.destroy$)).subscribe({
      next: (product) => {
        if (this.selectedProduct) {
          const index = this.products.findIndex(p => p.id === product.id);
          if (index !== -1) {
            this.products[index] = product;
          }
        } else {
          this.products.unshift(product);
        }
        this.cancelEditProduct();
        this.successMessage = this.selectedProduct ? 'Producto actualizado' : 'Producto creado';
        this.isSubmittingProduct = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'Error al guardar el producto';
        this.isSubmittingProduct = false;
      }
    });
  }

  deleteProduct(product: Product): void {
    if (confirm(`¿Estás seguro de que deseas eliminar "${product.name}"?`)) {
      this.productService.deleteProduct(product.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.products = this.products.filter(p => p.id !== product.id);
            this.successMessage = 'Producto eliminado';
            setTimeout(() => this.successMessage = '', 3000);
          },
          error: (error) => {
            this.errorMessage = error.error?.message || 'Error al eliminar el producto';
          }
        });
    }
  }

  cancelEditProduct(): void {
    this.isEditingProduct = false;
    this.selectedProduct = null;
    this.productForm.reset({ unit: 'g' });
  }

  nextProductPage(): void {
    if (this.currentProductPage < this.totalProductPages - 1) {
      this.currentProductPage++;
      this.loadProducts();
    }
  }

  previousProductPage(): void {
    if (this.currentProductPage > 0) {
      this.currentProductPage--;
      this.loadProducts();
    }
  }

  // ==================== ÓRDENES ====================

  switchToOrders(): void {
    this.activeTab = 'orders';
    this.loadOrders();
  }

  loadOrders(): void {
    this.isLoadingOrders = true;
    
    let request = this.orderStatusFilter
      ? this.orderService.getOrdersByStatus(this.orderStatusFilter, this.currentOrderPage, 10)
      : this.orderService.getAllOrders(this.currentOrderPage, 10);

    request.pipe(takeUntil(this.destroy$)).subscribe({
      next: (response: PagedResponse<Order>) => {
        this.orders = response.content;
        this.totalOrderPages = response.totalPages;
        this.isLoadingOrders = false;
      },
      error: (error) => {
        this.errorMessage = 'Error al cargar órdenes';
        this.isLoadingOrders = false;
      }
    });
  }

  filterOrdersByStatus(): void {
    this.currentOrderPage = 0;
    this.loadOrders();
  }

  updateOrderStatus(order: Order, newStatus: OrderStatus): void {
    this.orderService.updateOrderStatus(order.id, newStatus)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (updatedOrder) => {
          const index = this.orders.findIndex(o => o.id === order.id);
          if (index !== -1) {
            this.orders[index] = updatedOrder;
          }
          this.successMessage = 'Estado de orden actualizado';
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (error) => {
          this.errorMessage = error.error?.message || 'Error al actualizar el estado';
        }
      });
  }

  nextOrderPage(): void {
    if (this.currentOrderPage < this.totalOrderPages - 1) {
      this.currentOrderPage++;
      this.loadOrders();
    }
  }

  previousOrderPage(): void {
    if (this.currentOrderPage > 0) {
      this.currentOrderPage--;
      this.loadOrders();
    }
  }

  viewOrderDetails(order: Order): void {
    // Placeholder para ver detalles de orden
    console.log('Detalles de orden:', order);
  }

  $any(value: any): any {
    return value;
  }

  // ==================== HELPERS ====================

  getCategoryName(categoryId: number): string {
    const category = this.categories.find(c => c.id === categoryId);
    return category ? category.name : 'Desconocida';
  }

  getStatusLabel(status: OrderStatus): string {
    const labels: { [key in OrderStatus]: string } = {
      [OrderStatus.PENDING]: 'Pendiente',
      [OrderStatus.CONFIRMED]: 'Confirmada',
      [OrderStatus.PROCESSING]: 'En Proceso',
      [OrderStatus.SHIPPED]: 'Enviada',
      [OrderStatus.DELIVERED]: 'Entregada',
      [OrderStatus.CANCELLED]: 'Cancelada',
      [OrderStatus.REFUNDED]: 'Reembolsada'
    };
    return labels[status] || status;
  }

  getStatusColor(status: OrderStatus): string {
    const colors: { [key in OrderStatus]: string } = {
      [OrderStatus.PENDING]: 'warning',
      [OrderStatus.CONFIRMED]: 'info',
      [OrderStatus.PROCESSING]: 'info',
      [OrderStatus.SHIPPED]: 'primary',
      [OrderStatus.DELIVERED]: 'success',
      [OrderStatus.CANCELLED]: 'danger',
      [OrderStatus.REFUNDED]: 'secondary'
    };
    return colors[status] || 'secondary';
  }
}
