import { Component, OnInit, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';

import { RouterModule, Router } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { CategoryService } from '../../services/category.service';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';
import { Product, Category } from '../../models/product.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class HomeComponent implements OnInit {
signup() {
  this.router.navigate(['/login']);
}
  
  // Categorías de té (cargadas desde el backend)
  categories: Category[] = [];

  // Productos destacados (cargados desde el backend)
  featuredProducts: Product[] = [];
  allProducts: Product[] = [];
  filteredProducts: Product[] = [];
  searchTerm = '';
  filterCategoryId: number | null = null;
  sortMode = '';
  showAllProducts = false;

  // Estado del componente
  isScrolled = false;
  selectedCategory: string | null = null;
  newsletterEmail = '';
  newsletterSubmitted = false;
  loading = true;
  loadingCategories = true;
  loadingProducts = true;
  error: string | null = null;
  
  // i18n template properties
  get availableLabel(): string { return $localize`:home.available@@home.available:Disponible`; }
  get soldOutLabel(): string { return $localize`:home.soldOut@@home.soldOut:Agotado`; }
  get describeVariety(): string { return $localize`:home.describeVariety@@home.describeVariety:Descubre esta variedad`; }
  
  // Timestamps para mostrar cuando se obtuvieron los datos
  categoriesLoadTime: string | null = null;
  productsLoadTime: string | null = null;

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService,
    public cartService: CartService,
    public authService: AuthService,
    public router: Router
  ) { }

  ngOnInit(): void {
    // Cargar datos desde el backend
    this.loadCategories();
    this.loadFeaturedProducts();
    
    // Inicialización de animaciones cuando se carga el componente
    this.initScrollAnimations();
  }

  /**
   * Cargar categorías desde el backend
   */
  loadCategories(): void {
    this.loadingCategories = true;
    this.categoryService.getActiveCategories().subscribe({
      next: (categories) => {
        if (categories && categories.length > 0) {
          this.categories = categories;
          this.categoriesLoadTime = new Date().toLocaleTimeString('es-ES');
        } else {
          console.warn('⚠️ No se encontraron categorías en el servidor');
          this.categories = this.getExampleCategories();
        }
        this.loadingCategories = false;
      },
      error: (err) => {
        console.error('❌ Error cargando categorías del API:', err.message || err);
        this.categories = this.getExampleCategories();
        this.loadingCategories = false;
      }
    });
  }

  /**
   * Cargar productos desde el backend
   */
  loadFeaturedProducts(): void {
    this.loadingProducts = true;
    this.error = null;
    this.productService.getActiveProducts().subscribe({
      next: (products) => {
        if (products && products.length > 0) {
          this.allProducts = products;
          // Limitar a 8 productos
          this.featuredProducts = products.slice(0, 8);
          this.filteredProducts = this.featuredProducts;
          this.productsLoadTime = new Date().toLocaleTimeString('es-ES');
          this.loadingProducts = false;
          this.loading = false;
        } else {
          console.warn('⚠️ No se encontraron productos en el servidor');
          this.loadingProducts = false;
          this.loading = false;
          this.featuredProducts = this.getExampleProducts();
          this.allProducts = this.featuredProducts;
          this.filteredProducts = this.featuredProducts;
        }
      },
      error: (err) => {
        console.error('❌ Error cargando productos del API:', err.message || err);
        this.loadingProducts = false;
        this.loading = false;
        this.featuredProducts = this.getExampleProducts();
        this.allProducts = this.featuredProducts;
        this.filteredProducts = this.featuredProducts;
      }
    });
  }

  /**
   * Detecta el scroll para activar animaciones
   */
  @HostListener('window:scroll')
  onWindowScroll(): void {
    const scrollPosition = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0;
    this.isScrolled = scrollPosition > 100;
    
    // Activar animaciones de elementos al hacer scroll
    this.revealOnScroll();
  }

  /**
   * Inicializa las animaciones de scroll
   */
  private initScrollAnimations(): void {
    // Observer para animar elementos cuando entran en el viewport
    const observerOptions = {
      threshold: 0.1,
      rootMargin: '0px 0px -50px 0px'
    };

    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.classList.add('visible');
        }
      });
    }, observerOptions);

    // Observar secciones
    setTimeout(() => {
      const sections = document.querySelectorAll('.section-header, .category-card, .product-card, .value-card');
      sections.forEach(section => observer.observe(section));
    }, 100);
  }

  /**
   * Revela elementos al hacer scroll
   */
  private revealOnScroll(): void {
    const reveals = document.querySelectorAll('[data-reveal]');
    
    reveals.forEach(element => {
      const windowHeight = window.innerHeight;
      const elementTop = element.getBoundingClientRect().top;
      const elementVisible = 150;
      
      if (elementTop < windowHeight - elementVisible) {
        element.classList.add('active');
      }
    });
  }

  /**
   * Navega a la colección de una categoría
   */
  exploreCategory(categoryId: number): void {
    this.selectedCategory = categoryId.toString();
    // Aquí navegarías a la página de productos filtrados por categoría
    // this.router.navigate(['/products'], { queryParams: { category: categoryId } });
  }

  /**
   * Navega a la página de inicio (scroll al hero)
   */
  exploreCollection(): void {
    this.scrollToSection('productos');
  }

  /**
   * Navega a la página "Acerca de"
   */
  learnMore(): void {
    this.scrollToSection('nosotros');
  }

  /**
   * Añade un producto al carrito usando el servicio
   */
  addToCart(product: Product): void {
    this.cartService.addProduct(product.id, 1, product).subscribe({
      next: () => {
        this.showNotification($localize`:home.addedToCart@@home.addedToCart:${product.name}:name: añadido al carrito`, 'success');
      },
      error: (err) => {
        console.error('Error añadiendo al carrito:', err);
        this.showNotification($localize`:home.addToCartError@@home.addToCartError:Error al añadir al carrito`, 'error');
      }
    });
  }

  /**
   * Genera el array de estrellas para el rating
   */
  getStars(rating: number): string[] {
    const stars: string[] = [];
    for (let i = 0; i < 5; i++) {
      stars.push(i < rating ? '★' : '☆');
    }
    return stars;
  }

  /**
   * Maneja el envío del formulario de newsletter
   */
  subscribeNewsletter(): void {
    if (!this.newsletterEmail || !this.isValidEmail(this.newsletterEmail)) {
      this.showNotification($localize`:home.invalidEmail@@home.invalidEmail:Por favor, introduce un email válido`, 'error');
      return;
    }

    // Aquí integrarías con tu servicio de newsletter
    // this.newsletterService.subscribe(this.newsletterEmail);

    this.newsletterSubmitted = true;
    this.showNotification($localize`:home.newsletterThanks@@home.newsletterThanks:¡Gracias por suscribirte!`, 'success');
    this.newsletterEmail = '';

    setTimeout(() => {
      this.newsletterSubmitted = false;
    }, 3000);
  }

  /**
   * Valida un email
   */
  private isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  /**
   * Muestra una notificación (implementar con servicio de notificaciones)
   */
  private showNotification(message: string, type: 'success' | 'error' | 'info' = 'info'): void {
    // TODO: integrate with a toast/notification service
    // this.notificationService.show(message, type);
  }

  /**
   * Scroll suave a una sección
   */
  scrollToSection(sectionId: string): void {
    const element = document.getElementById(sectionId);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }

  /**
   * Formatea el precio con el símbolo de euro
   */
  formatPrice(price: number): string {
    return price.toFixed(2) + '€';
  }

  /**
   * Trackby function para mejorar rendimiento en ngFor
   */
  trackByProductId(index: number, product: Product): number {
    return product.id;
  }

  /**
   * Trackby function para categorías
   */
  trackByCategoryId(index: number, category: Category): number {
    return category.id;
  }

  /**
   * Categorías de ejemplo (fallback)
   */
  private getExampleCategories(): Category[] {
    return [
      { id: 1, name: 'Té Verde', description: 'Frescura y vitalidad en cada taza' },
      { id: 2, name: 'Té Negro', description: 'Cuerpo intenso y carácter robusto' },
      { id: 3, name: 'Té Blanco', description: 'Delicadeza y sutileza ancestral' },
      { id: 4, name: 'Oolong', description: 'La perfecta armonía entre mundos' },
      { id: 5, name: 'Infusiones', description: 'Bienestar natural sin cafeína' },
      { id: 6, name: 'Matcha', description: 'Energía concentrada de tradición' }
    ];
  }

  /**
   * Productos de ejemplo (fallback)
   */
  private getExampleProducts(): Product[] {
    return [
      {
        id: 1,
        active: true,
        name: 'Dragon Well Premium',
        description: 'Té verde de alta calidad',
        price: 28.50,
        category: { id: 1, name: 'Té Verde', description: '' },
        imageUrl: ''
      }
    ];
  }

  // ---- Filter/Search methods ----

  onSearchInput(event: Event): void {
    this.searchTerm = (event.target as HTMLInputElement).value;
    this.applyFilters();
  }

  onCategoryFilter(event: Event): void {
    const val = (event.target as HTMLSelectElement).value;
    this.filterCategoryId = val ? +val : null;
    this.applyFilters();
  }

  onSortChange(event: Event): void {
    this.sortMode = (event.target as HTMLSelectElement).value;
    this.applyFilters();
  }

  toggleShowAll(): void {
    this.showAllProducts = !this.showAllProducts;
    this.applyFilters();
  }

  private applyFilters(): void {
    let source = this.showAllProducts || this.searchTerm || this.filterCategoryId
      ? this.allProducts
      : this.featuredProducts;

    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      source = source.filter(p => p.name.toLowerCase().includes(term) || p.description?.toLowerCase().includes(term));
    }

    if (this.filterCategoryId) {
      source = source.filter(p => p.category?.id === this.filterCategoryId);
    }

    if (this.sortMode === 'price-asc') {
      source = [...source].sort((a, b) => a.price - b.price);
    } else if (this.sortMode === 'price-desc') {
      source = [...source].sort((a, b) => b.price - a.price);
    } else if (this.sortMode === 'name') {
      source = [...source].sort((a, b) => a.name.localeCompare(b.name));
    }

    this.filteredProducts = source;
  }
}