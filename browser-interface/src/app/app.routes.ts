import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home';
import { ProfileComponent } from './components/profile/profile';
import { ProductComponent } from './components/product/product';
import { OrdersComponent } from './components/orders/orders';
import { AdminComponent } from './components/admin/admin';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'home', component: HomeComponent },
  { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard] },
  { path: 'product/:id', component: ProductComponent },
  { path: 'orders', component: OrdersComponent, canActivate: [AuthGuard] },
  { path: 'admin', component: AdminComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: '' }
];
