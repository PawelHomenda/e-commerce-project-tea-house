import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home';
import { ProfileComponent } from './components/profile/profile';
import { ProductComponent } from './components/product/product';
import { OrdersComponent } from './components/orders/orders';
import { AdminComponent } from './components/admin/admin';
import { LoginComponent } from './components/login/login.component';
import { AuthorizedComponent } from './components/authorized/authorized.component';
import { CartComponent } from './components/cart/cart';
import { CheckoutComponent } from './components/checkout/checkout';
import { OrderConfirmationComponent } from './components/order-confirmation/order-confirmation';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'authorized', component: AuthorizedComponent },
  { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard] },
  { path: 'product/:id', component: ProductComponent },
  { path: 'cart', component: CartComponent },
  { path: 'checkout', component: CheckoutComponent, canActivate: [AuthGuard] },
  { path: 'order-confirmation/:id', component: OrderConfirmationComponent, canActivate: [AuthGuard] },
  { path: 'orders', component: OrdersComponent, canActivate: [AuthGuard] },
  { path: 'admin', component: AdminComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: '' }
];
