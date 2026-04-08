import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProductService } from '../../services/product.service';
import { CategoryService } from '../../services/category.service';
import { OrderService } from '../../services/order.service';
import { AuthService } from '../../services/auth.service';
import { AdminService } from '../../services/admin.service';
import { EmployeeService } from '../../services/employee.service';
import { ProviderService } from '../../services/provider.service';
import { Product, ProductDTO, Category, PagedResponse, Client, Employee, Provider, OrderClient } from '../../models/product.model';
import { OrderStatus } from '../../models/order.model';
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
  activeTab: 'products' | 'orders' | 'clients' | 'employees' | 'providers' | 'inventory' = 'products';

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
  orders: OrderClient[] = [];
  orderStatusFilter: OrderStatus | '' = '';
  isLoadingOrders = true;
  currentOrderPage = 0;
  totalOrderPages = 0;

  // Clientes
  clients: Client[] = [];
  clientForm!: FormGroup;
  isEditingClient = false;
  selectedClient: Client | null = null;
  isLoadingClients = true;
  isSubmittingClient = false;
  currentClientPage = 0;
  totalClientPages = 0;

  // Empleados
  employees: Employee[] = [];
  employeeForm!: FormGroup;
  isEditingEmployee = false;
  selectedEmployee: Employee | null = null;
  isLoadingEmployees = true;
  isSubmittingEmployee = false;
  currentEmployeePage = 0;
  totalEmployeePages = 0;

  // Proveedores
  providers: Provider[] = [];
  providerForm!: FormGroup;
  isEditingProvider = false;
  selectedProvider: Provider | null = null;
  isLoadingProviders = true;
  isSubmittingProvider = false;
  currentProviderPage = 0;
  totalProviderPages = 0;

  // Mensajes
  successMessage = '';
  errorMessage = '';

  // i18n template properties
  get editProductLabel(): string { return $localize`:admin.editProduct@@admin.editProduct:Editar Producto`; }
  get newProductLabel(): string { return $localize`:admin.newProduct@@admin.newProduct:Nuevo Producto`; }
  get savingProductLabel(): string { return $localize`:admin.savingProduct@@admin.savingProduct:Guardando...`; }
  get saveProductLabel(): string { return $localize`:admin.saveProduct@@admin.saveProduct:Guardar Producto`; }
  get editEmployeeLabel(): string { return $localize`:admin.editEmployee@@admin.editEmployee:Editar Empleado`; }
  get newEmployeeLabel(): string { return $localize`:admin.newEmployee@@admin.newEmployee:Nuevo Empleado`; }
  get editProviderLabel(): string { return $localize`:admin.editProvider@@admin.editProvider:Editar Proveedor`; }
  get newProviderLabel(): string { return $localize`:admin.newProvider@@admin.newProvider:Nuevo Proveedor`; }
  get savingLabel(): string { return $localize`:admin.saving@@admin.saving:Guardando...`; }
  get saveLabel(): string { return $localize`:admin.save@@admin.save:Guardar`; }
  get tableLabel(): string { return $localize`:admin.serviceTable@@admin.serviceTable:En Mesa`; }
  get takeawayLabel(): string { return $localize`:admin.serviceTakeaway@@admin.serviceTakeaway:Para Llevar`; }
  get deliveryLabel(): string { return $localize`:admin.serviceDelivery@@admin.serviceDelivery:A Domicilio`; }

  private destroy$ = new Subject<void>();

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService,
    public orderService: OrderService,
    private authService: AuthService,
    private adminService: AdminService,
    private employeeService: EmployeeService,
    private providerService: ProviderService,
    private fb: FormBuilder
  ) {
    this.initializeProductForm();
    this.initializeClientForm();
    this.initializeEmployeeForm();
    this.initializeProviderForm();
  }

  ngOnInit(): void {
    // Verificar si es admin
    if (!this.authService.isAdmin()) {
      this.errorMessage = $localize`:admin.noAdminPermission@@admin.noAdminPermission:No tienes permisos de administrador`;
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
      imageUrl: [''],
      thumbnailUrl: [''],
      origin: ['', Validators.required],
      weight: ['', [Validators.required, Validators.min(0.1)]],
      unit: ['g', Validators.required]
    });
  }

  private initializeClientForm(): void {
    this.clientForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: [''],
      address: [''],
      city: [''],
      state: [''],
      postalCode: [''],
      country: ['']
    });
  }

  private initializeEmployeeForm(): void {
    this.employeeForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: [''],
      address: [''],
      salary: ['', [Validators.required, Validators.min(0)]],
      position: [''],
      department: ['']
    });
  }

  private initializeProviderForm(): void {
    this.providerForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      contact: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: [''],
      address: [''],
      city: [''],
      state: [''],
      postalCode: [''],
      country: ['']
    });
  }

  // ==================== PRODUCTOS ====================

  loadCategories(): void {
    this.categoryService.getActiveCategories()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (categories: Category[]) => {
          this.categories = categories;
        },
        error: (error: any) => {
          console.error('Error loading categories:', error);
        }
      });
  }

  loadProducts(): void {
    this.isLoadingProducts = true;
    this.productService.getAllProducts()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (products: Product[]) => {
          this.products = products;
          this.totalProductPages = 1;
          this.isLoadingProducts = false;
        },
        error: (error: any) => {
          this.errorMessage = $localize`:admin.loadProductsError@@admin.loadProductsError:Error al cargar productos`;
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
      this.errorMessage = $localize`:admin.fieldsRequired@@admin.fieldsRequired:Por favor completa todos los campos requeridos`;
      return;
    }

    this.isSubmittingProduct = true;
    const productDTO: ProductDTO = this.productForm.value;

    const request = this.selectedProduct 
      ? this.productService.updateProduct(this.selectedProduct.id, productDTO)
      : this.productService.createProduct(productDTO);

    request.pipe(takeUntil(this.destroy$)).subscribe({
      next: (product: Product | null) => {
        if (this.selectedProduct && product) {
          const index = this.products.findIndex(p => p.id === product.id);
          if (index !== -1) {
            this.products[index] = product;
          }
        } else if (product) {
          this.products.unshift(product);
        }
        this.cancelEditProduct();
        this.successMessage = this.selectedProduct ? $localize`:admin.productUpdated@@admin.productUpdated:Producto actualizado` : $localize`:admin.productCreated@@admin.productCreated:Producto creado`;
        this.isSubmittingProduct = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error: any) => {
          this.errorMessage = error.error?.message || $localize`:admin.saveProductError@@admin.saveProductError:Error al guardar el producto`;
        this.isSubmittingProduct = false;
      }
    });
  }

  deleteProduct(product: Product): void {
    if (confirm($localize`:admin.confirmDeleteProduct@@admin.confirmDeleteProduct:¿Estás seguro de que deseas eliminar "${product.name}:name:"?`)) {
      this.productService.deleteProduct(product.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.products = this.products.filter(p => p.id !== product.id);
            this.successMessage = $localize`:admin.productDeleted@@admin.productDeleted:Producto eliminado`;
            setTimeout(() => this.successMessage = '', 3000);
          },
          error: (error) => {
            this.errorMessage = error.error?.message || $localize`:admin.deleteProductError@@admin.deleteProductError:Error al eliminar el producto`;
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
      ? this.orderService.getOrdersByStatus(this.orderStatusFilter)
      : this.orderService.getAllOrders();

    request.pipe(takeUntil(this.destroy$)).subscribe({
      next: (orders: any[]) => {
        this.orders = orders as OrderClient[];
        this.isLoadingOrders = false;
      },
      error: (error) => {
          this.errorMessage = $localize`:admin.loadOrdersError@@admin.loadOrdersError:Error al cargar órdenes`;
        this.isLoadingOrders = false;
      }
    });
  }

  filterOrdersByStatus(): void {
    this.currentOrderPage = 0;
    this.loadOrders();
  }

  updateOrderStatus(order: OrderClient, newStatus: OrderStatus): void {
    this.orderService.updateOrderStatus(order.id, newStatus)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (updatedOrder: any) => {
          const index = this.orders.findIndex(o => o.id === order.id);
          if (index !== -1) {
            this.orders[index] = updatedOrder as OrderClient;
          }
          this.successMessage = $localize`:admin.orderStatusUpdated@@admin.orderStatusUpdated:Estado de orden actualizado`;
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (error) => {
          this.errorMessage = error.error?.message || $localize`:admin.updateStatusError@@admin.updateStatusError:Error al actualizar el estado`;
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

  viewOrderDetails(order: OrderClient): void {
    // Placeholder para ver detalles de orden
    console.log('Detalles de orden:', order);
  }

  $any(value: any): any {
    return value;
  }

  // ==================== HELPERS ====================

  getCategoryName(categoryId: number): string {
    const category = this.categories.find(c => c.id === categoryId);
    return category ? category.name : $localize`:admin.unknownCategory@@admin.unknownCategory:Desconocida`;
  }

  getStatusLabel(status: OrderStatus): string {
    const labels: { [key in OrderStatus]: string } = {
      [OrderStatus.PENDENT]: $localize`:admin.statusPendent@@admin.statusPendent:Pendiente`,
      [OrderStatus.PREPARING]: $localize`:admin.statusPreparing@@admin.statusPreparing:En Preparación`,
      [OrderStatus.DELIVERED]: $localize`:admin.statusDelivered@@admin.statusDelivered:Entregada`,
      [OrderStatus.CANCELED]: $localize`:admin.statusCanceled@@admin.statusCanceled:Cancelada`
    };
    return labels[status] || status;
  }

  getStatusColor(status: OrderStatus): string {
    const colors: { [key in OrderStatus]: string } = {
      [OrderStatus.PENDENT]: 'warning',
      [OrderStatus.PREPARING]: 'info',
      [OrderStatus.DELIVERED]: 'success',
      [OrderStatus.CANCELED]: 'danger'
    };
    return colors[status] || 'secondary';
  }

  // ==================== CLIENTES ====================

  switchToClients(): void {
    this.activeTab = 'clients';
    this.loadClients();
  }

  loadClients(): void {
    this.isLoadingClients = true;
    this.adminService.getClients()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (clients: Client[]) => {
          this.clients = clients;
          this.isLoadingClients = false;
        },
        error: (error) => {
          this.errorMessage = $localize`:admin.loadClientsError@@admin.loadClientsError:Error al cargar clientes`;
          this.isLoadingClients = false;
        }
      });
  }

  editClient(client: Client): void {
    this.isEditingClient = true;
    this.selectedClient = client;
    this.clientForm.patchValue(client);
  }

  submitClient(): void {
    if (this.clientForm.invalid) {
      this.errorMessage = $localize`:admin.fieldsRequired@@admin.fieldsRequired:Por favor completa todos los campos requeridos`;
      return;
    }

    if (!this.selectedClient) {
      this.errorMessage = $localize`:admin.selectClient@@admin.selectClient:Selecciona un cliente para actualizar`;
      return;
    }

    this.isSubmittingClient = true;
    const clientData = this.clientForm.value;

    this.adminService.updateClient(this.selectedClient.id, clientData)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.cancelEditClient();
          this.loadClients();
          this.successMessage = $localize`:admin.clientUpdated@@admin.clientUpdated:Cliente actualizado`;
          this.isSubmittingClient = false;
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (error: any) => {
          this.errorMessage = error.error?.message || $localize`:admin.saveClientError@@admin.saveClientError:Error al guardar cliente`;
          this.isSubmittingClient = false;
        }
      });
  }

  deleteClient(client: Client): void {
    if (confirm($localize`:admin.confirmDeleteClient@@admin.confirmDeleteClient:¿Estás seguro de que deseas eliminar a "${client.firstName}:firstName: ${client.lastName}:lastName:"?`)) {
      this.adminService.deleteClient(client.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.clients = this.clients.filter(c => c.id !== client.id);
            this.successMessage = $localize`:admin.clientDeleted@@admin.clientDeleted:Cliente eliminado`;
            setTimeout(() => this.successMessage = '', 3000);
          },
          error: (error) => {
            this.errorMessage = $localize`:admin.deleteClientError@@admin.deleteClientError:Error al eliminar cliente`;
          }
        });
    }
  }

  cancelEditClient(): void {
    this.isEditingClient = false;
    this.selectedClient = null;
    this.clientForm.reset();
  }

  nextClientPage(): void {
    if (this.currentClientPage < this.totalClientPages - 1) {
      this.currentClientPage++;
      this.loadClients();
    }
  }

  previousClientPage(): void {
    if (this.currentClientPage > 0) {
      this.currentClientPage--;
      this.loadClients();
    }
  }

  // ==================== EMPLEADOS ====================

  switchToEmployees(): void {
    this.activeTab = 'employees';
    this.loadEmployees();
  }

  loadEmployees(): void {
    this.isLoadingEmployees = true;
    this.employeeService.getAll()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (employees: Employee[]) => {
          this.employees = employees;
          this.isLoadingEmployees = false;
        },
        error: (error) => {
          this.errorMessage = $localize`:admin.loadEmployeesError@@admin.loadEmployeesError:Error al cargar empleados`;
          this.isLoadingEmployees = false;
        }
      });
  }

  addEmployeeForm(): void {
    this.isEditingEmployee = true;
    this.selectedEmployee = null;
    this.employeeForm.reset();
  }

  editEmployee(employee: Employee): void {
    this.isEditingEmployee = true;
    this.selectedEmployee = employee;
    this.employeeForm.patchValue(employee);
  }

  submitEmployee(): void {
    if (this.employeeForm.invalid) {
      this.errorMessage = $localize`:admin.fieldsRequired@@admin.fieldsRequired:Por favor completa todos los campos requeridos`;
      return;
    }

    this.isSubmittingEmployee = true;
    const employeeData = this.employeeForm.value;

    const request = this.selectedEmployee
      ? this.employeeService.update(this.selectedEmployee.id, employeeData)
      : this.employeeService.create(employeeData);

    request.pipe(takeUntil(this.destroy$)).subscribe({
      next: (_: Employee) => {
        this.cancelEditEmployee();
        this.loadEmployees();
        this.successMessage = this.selectedEmployee ? $localize`:admin.employeeUpdated@@admin.employeeUpdated:Empleado actualizado` : $localize`:admin.employeeCreated@@admin.employeeCreated:Empleado creado`;
        this.isSubmittingEmployee = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error: any) => {
          this.errorMessage = error.error?.message || $localize`:admin.saveEmployeeError@@admin.saveEmployeeError:Error al guardar empleado`;
        this.isSubmittingEmployee = false;
      }
    });
  }

  deleteEmployee(employee: Employee): void {
    if (confirm($localize`:admin.confirmDeleteEmployee@@admin.confirmDeleteEmployee:¿Estás seguro de que deseas eliminar a "${employee.firstName}:firstName: ${employee.lastName}:lastName:"?`)) {
      this.employeeService.delete(employee.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.employees = this.employees.filter(e => e.id !== employee.id);
            this.successMessage = $localize`:admin.employeeDeleted@@admin.employeeDeleted:Empleado eliminado`;
            setTimeout(() => this.successMessage = '', 3000);
          },
          error: (error) => {
            this.errorMessage = $localize`:admin.deleteEmployeeError@@admin.deleteEmployeeError:Error al eliminar empleado`;
          }
        });
    }
  }

  cancelEditEmployee(): void {
    this.isEditingEmployee = false;
    this.selectedEmployee = null;
    this.employeeForm.reset();
  }

  nextEmployeePage(): void {
    if (this.currentEmployeePage < this.totalEmployeePages - 1) {
      this.currentEmployeePage++;
      this.loadEmployees();
    }
  }

  previousEmployeePage(): void {
    if (this.currentEmployeePage > 0) {
      this.currentEmployeePage--;
      this.loadEmployees();
    }
  }

  // ==================== PROVEEDORES ====================

  switchToProviders(): void {
    this.activeTab = 'providers';
    this.loadProviders();
  }

  loadProviders(): void {
    this.isLoadingProviders = true;
    this.providerService.getAll()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (providers: Provider[]) => {
          this.providers = providers;
          this.isLoadingProviders = false;
        },
        error: (error) => {
          this.errorMessage = $localize`:admin.loadProvidersError@@admin.loadProvidersError:Error al cargar proveedores`;
          this.isLoadingProviders = false;
        }
      });
  }

  addProviderForm(): void {
    this.isEditingProvider = true;
    this.selectedProvider = null;
    this.providerForm.reset();
  }

  editProvider(provider: Provider): void {
    this.isEditingProvider = true;
    this.selectedProvider = provider;
    this.providerForm.patchValue(provider);
  }

  submitProvider(): void {
    if (this.providerForm.invalid) {
      this.errorMessage = $localize`:admin.fieldsRequired@@admin.fieldsRequired:Por favor completa todos los campos requeridos`;
      return;
    }

    this.isSubmittingProvider = true;
    const providerData = this.providerForm.value;

    const request = this.selectedProvider
      ? this.providerService.update(this.selectedProvider.id, providerData)
      : this.providerService.create(providerData);

    request.pipe(takeUntil(this.destroy$)).subscribe({
      next: (_: Provider) => {
        this.cancelEditProvider();
        this.loadProviders();
        this.successMessage = this.selectedProvider ? $localize`:admin.providerUpdated@@admin.providerUpdated:Proveedor actualizado` : $localize`:admin.providerCreated@@admin.providerCreated:Proveedor creado`;
        this.isSubmittingProvider = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error: any) => {
          this.errorMessage = error.error?.message || $localize`:admin.saveProviderError@@admin.saveProviderError:Error al guardar proveedor`;
        this.isSubmittingProvider = false;
      }
    });
  }

  deleteProvider(provider: Provider): void {
    if (confirm($localize`:admin.confirmDeleteProvider@@admin.confirmDeleteProvider:¿Estás seguro de que deseas eliminar a "${provider.name}:name:"?`)) {
      this.providerService.delete(provider.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.providers = this.providers.filter(p => p.id !== provider.id);
            this.successMessage = $localize`:admin.providerDeleted@@admin.providerDeleted:Proveedor eliminado`;
            setTimeout(() => this.successMessage = '', 3000);
          },
          error: (error) => {
            this.errorMessage = $localize`:admin.deleteProviderError@@admin.deleteProviderError:Error al eliminar proveedor`;
          }
        });
    }
  }

  cancelEditProvider(): void {
    this.isEditingProvider = false;
    this.selectedProvider = null;
    this.providerForm.reset();
  }

  nextProviderPage(): void {
    if (this.currentProviderPage < this.totalProviderPages - 1) {
      this.currentProviderPage++;
      this.loadProviders();
    }
  }

  previousProviderPage(): void {
    if (this.currentProviderPage > 0) {
      this.currentProviderPage--;
      this.loadProviders();
    }
  }
}
