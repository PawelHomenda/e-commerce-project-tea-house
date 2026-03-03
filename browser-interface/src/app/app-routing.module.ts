import { AuthGuard } from './guards/auth.guard';
import { AdminGuard } from './guards/admin.guard';
import { HomeComponent } from './components/home/home';
import { ProductComponent } from './components/product/product';   // ← corrected path
import { ProfileComponent } from './components/profile/profile';
import { OrdersComponent } from './components/orders/orders';
import { AdminComponent } from './components/admin/admin';
import { Routes } from '@angular/router';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'products', component: ProductComponent },
  
  // Rutas protegidas (requieren login)
  { 
    path: 'profile', 
    component: ProfileComponent,
    canActivate: [AuthGuard]  // ← Protegida
  },
  { 
    path: 'orders', 
    component: OrdersComponent,
    canActivate: [AuthGuard]  // ← Protegida
  },
  
  // Rutas de admin
  { 
    path: 'admin', 
    component: AdminComponent,
    canActivate: [AdminGuard]  // ← Solo admins
  }
];