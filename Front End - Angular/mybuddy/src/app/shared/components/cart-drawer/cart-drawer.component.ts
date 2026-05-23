import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CartService } from '@core/services/cart.service';

@Component({
  selector: 'app-cart-drawer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cart-drawer.component.html',
  styleUrl: './cart-drawer.component.scss'
})
export class CartDrawerComponent {
  cartService = inject(CartService);

  get isOpen() {
    return this.cartService.isDrawerOpen();
  }

  get items() {
    return this.cartService.cartItems();
  }

  get totalItems() {
    return this.cartService.totalItems();
  }

  get totalPrice() {
    return this.cartService.totalPrice();
  }

  close() {
    this.cartService.closeDrawer();
  }

  removeItem(id: number) {
    this.cartService.removeFromCart(id);
  }

  updateQuantity(id: number, qty: number) {
    this.cartService.updateQuantity(id, qty);
  }
}
