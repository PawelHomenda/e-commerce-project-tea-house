import { Component, OnInit, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { Product, Category } from '../../models/product.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class HomeComponent implements OnInit {
  
  // Categorías de té (cargadas desde el backend)
  categories: Category[] = [];

  // Productos destacados (cargados desde el backend)
  featuredProducts: Product[] = [];

  // Estado del componente
  isScrolled = false;
  selectedCategory: string | null = null;
  newsletterEmail = '';
  newsletterSubmitted = false;
  loading = true;
  error: string | null = null;

  constructor(
    private productService: ProductService,
    private cartService: CartService
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
    this.productService.getAllCategories().subscribe({
      next: (categories) => {
        this.categories = categories;
        console.log('Categorías cargadas:', categories);
      },
      error: (err) => {
        console.error('Error cargando categorías:', err);
        this.error = 'No se pudieron cargar las categorías';
        // Fallback a categorías de ejemplo si falla
        this.categories = this.getExampleCategories();
      }
    });
  }

  /**
   * Cargar productos destacados desde el backend
   */
  loadFeaturedProducts(): void {
    this.loading = true;
    this.productService.getFeaturedProducts(8).subscribe({
      next: (products) => {
        this.featuredProducts = products;
        this.loading = false;
        console.log('Productos destacados cargados:', products);
      },
      error: (err) => {
        console.error('Error cargando productos:', err);
        this.error = 'No se pudieron cargar los productos';
        this.loading = false;
        // Fallback a productos de ejemplo si falla
        this.featuredProducts = this.getExampleProducts();
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
    console.log('Explorando categoría:', categoryId);
    // Aquí navegarías a la página de productos filtrados por categoría
    // this.router.navigate(['/products'], { queryParams: { category: categoryId } });
  }

  /**
   * Navega a la página de inicio (scroll al hero)
   */
  exploreCollection(): void {
    console.log('Explorando colección completa');
    // this.router.navigate(['/products']);
  }

  /**
   * Navega a la página "Acerca de"
   */
  learnMore(): void {
    console.log('Navegando a "Nuestra Historia"');
    // this.router.navigate(['/about']);
  }

  /**
   * Añade un producto al carrito usando el servicio
   */
  addToCart(product: Product): void {
    console.log('Añadiendo al carrito:', product);
    
    this.cartService.addProduct(product.id, 1).subscribe({
      next: (cart) => {
        this.showNotification(`${product.name} añadido al carrito`, 'success');
        console.log('Carrito actualizado:', cart);
      },
      error: (err) => {
        console.error('Error añadiendo al carrito:', err);
        this.showNotification('Error al añadir al carrito', 'error');
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
      this.showNotification('Por favor, introduce un email válido', 'error');
      return;
    }

    console.log('Suscripción a newsletter:', this.newsletterEmail);
    // Aquí integrarías con tu servicio de newsletter
    // this.newsletterService.subscribe(this.newsletterEmail);

    this.newsletterSubmitted = true;
    this.showNotification('¡Gracias por suscribirte!', 'success');
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
    console.log(`[${type.toUpperCase()}] ${message}`);
    // Aquí integrarías con tu servicio de notificaciones/toasts
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
   * Obtener badge del producto
   */
  getProductBadge(product: Product): string | null {
    if (product.isNew) return 'Nuevo';
    if (product.isBestseller) return 'Bestseller';
    if (product.isLimitedEdition) return 'Edición Limitada';
    return null;
  }

  /**
   * Obtener clase CSS del badge
   */
  getBadgeClass(product: Product): string {
    if (product.isBestseller) return 'bestseller';
    return '';
  }

  /**
   * Categorías de ejemplo (fallback)
   */
  private getExampleCategories(): Category[] {
    return [
      { id: 1, name: 'Té Verde', description: 'Frescura y vitalidad en cada taza', slug: 'te-verde', productCount: 12, imageUrl: '' },
      { id: 2, name: 'Té Negro', description: 'Cuerpo intenso y carácter robusto', slug: 'te-negro', productCount: 15, imageUrl: '' },
      { id: 3, name: 'Té Blanco', description: 'Delicadeza y sutileza ancestral', slug: 'te-blanco', productCount: 8, imageUrl: '' },
      { id: 4, name: 'Oolong', description: 'La perfecta armonía entre mundos', slug: 'oolong', productCount: 10, imageUrl: '' },
      { id: 5, name: 'Infusiones', description: 'Bienestar natural sin cafeína', slug: 'infusiones', productCount: 20, imageUrl: '' },
      { id: 6, name: 'Matcha', description: 'Energía concentrada de tradición', slug: 'matcha', productCount: 5, imageUrl: '' }
    ];
  }

  /**
   * Productos de ejemplo (fallback)
   */
  private getExampleProducts(): Product[] {
    return [
      {
        id: 1,
        name: 'Dragon Well Premium',
        description: 'Té verde de alta calidad',
        price: 28.50,
        stock: 50,
        category: { id: 1, name: 'Té Verde', description: '', slug: 'te-verde', productCount: 12 },
        imageUrl: '',
        origin: 'Hangzhou, China',
        weight: 100,
        unit: 'g',
        rating: 5,
        reviewCount: 127,
        isNew: true,
        isBestseller: false,
        isLimitedEdition: false,
        createdAt: new Date(),
        updatedAt: new Date()
      }
    ];
  }
}