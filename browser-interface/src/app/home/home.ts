import { Component, OnInit, HostListener } from '@angular/core';

interface Product {
  id: number;
  name: string;
  origin: string;
  price: number;
  rating: number;
  reviewCount: number;
  badge?: string;
  image?: string;
}

interface Category {
  id: string;
  name: string;
  description: string;
  count: number;
  color: string;
}

@Component({
  selector: 'app-home',
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class HomeComponent implements OnInit {

  // Categorías de té
  categories: Category[] = [
    {
      id: 'green',
      name: 'Té Verde',
      description: 'Frescura y vitalidad en cada taza',
      count: 12,
      color: '#56ab2f'
    },
    {
      id: 'black',
      name: 'Té Negro',
      description: 'Cuerpo intenso y carácter robusto',
      count: 15,
      color: '#000000'
    },
    {
      id: 'white',
      name: 'Té Blanco',
      description: 'Delicadeza y sutileza ancestral',
      count: 8,
      color: '#c3cfe2'
    },
    {
      id: 'oolong',
      name: 'Oolong',
      description: 'La perfecta armonía entre mundos',
      count: 10,
      color: '#f5576c'
    },
    {
      id: 'herbal',
      name: 'Infusiones',
      description: 'Bienestar natural sin cafeína',
      count: 20,
      color: '#00f2fe'
    },
    {
      id: 'matcha',
      name: 'Matcha',
      description: 'Energía concentrada de tradición',
      count: 5,
      color: '#38f9d7'
    }
  ];

  // Productos destacados
  featuredProducts: Product[] = [
    {
      id: 1,
      name: 'Dragon Well Premium',
      origin: 'Hangzhou, China',
      price: 28.50,
      rating: 5,
      reviewCount: 127,
      badge: 'Nuevo'
    },
    {
      id: 2,
      name: 'Silver Needle Organic',
      origin: 'Fujian, China',
      price: 42.00,
      rating: 5,
      reviewCount: 203,
      badge: 'Bestseller'
    },
    {
      id: 3,
      name: 'Darjeeling First Flush',
      origin: 'West Bengal, India',
      price: 35.50,
      rating: 4,
      reviewCount: 89
    },
    {
      id: 4,
      name: 'Ceremonial Matcha',
      origin: 'Uji, Japón',
      price: 52.00,
      rating: 5,
      reviewCount: 156,
      badge: 'Edición Limitada'
    }
  ];

  // Estado del componente
  isScrolled = false;
  selectedCategory: string | null = null;
  newsletterEmail = '';
  newsletterSubmitted = false;

  constructor() { }

  ngOnInit(): void {
    // Inicialización de animaciones cuando se carga el componente
    this.initScrollAnimations();
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
    const sections = document.querySelectorAll('.section-header, .category-card, .product-card, .value-card');
    sections.forEach(section => observer.observe(section));
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
  exploreCategory(categoryId: string): void {
    this.selectedCategory = categoryId;
    console.log('Explorando categoría:', categoryId);
    // Aquí irías a la página de productos filtrados por categoría
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
   * Añade un producto al carrito
   */
  addToCart(product: Product): void {
    console.log('Añadiendo al carrito:', product);
    // Aquí integrarías con tu servicio de carrito
    // this.cartService.addItem(product);

    // Feedback visual
    this.showNotification(`${product.name} añadido al carrito`);
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
  trackByCategoryId(index: number, category: Category): string {
    return category.id;
  }
}
