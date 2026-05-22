import { Injectable, computed, signal } from '@angular/core';

export interface CartItem {
  id: number;
  name: string;
  price: number;
  imageUrl: string;
  quantity: number;
  lojaNome?: string;
}

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private cartItemsSignal = signal<CartItem[]>([]);
  private isDrawerOpenSignal = signal<boolean>(false);

  // Expose signals
  readonly cartItems = this.cartItemsSignal.asReadonly();
  readonly isDrawerOpen = this.isDrawerOpenSignal.asReadonly();

  // Computed totals
  readonly totalItems = computed(() => this.cartItemsSignal().reduce((sum, item) => sum + item.quantity, 0));
  readonly totalPrice = computed(() => this.cartItemsSignal().reduce((sum, item) => sum + (item.price * item.quantity), 0));

  toggleDrawer(): void {
    this.isDrawerOpenSignal.update(state => !state);
  }

  openDrawer(): void {
    this.isDrawerOpenSignal.set(true);
  }

  closeDrawer(): void {
    this.isDrawerOpenSignal.set(false);
  }

  addToCart(item: Omit<CartItem, 'quantity'>): void {
    const currentItems = this.cartItemsSignal();
    const existingItem = currentItems.find(i => i.id === item.id);

    if (existingItem) {
      this.updateQuantity(item.id, existingItem.quantity + 1);
    } else {
      this.cartItemsSignal.set([...currentItems, { ...item, quantity: 1 }]);
    }
  }

  removeFromCart(itemId: number): void {
    this.cartItemsSignal.update(items => items.filter(i => i.id !== itemId));
  }

  updateQuantity(itemId: number, quantity: number): void {
    if (quantity <= 0) {
      this.removeFromCart(itemId);
      return;
    }
    this.cartItemsSignal.update(items => 
      items.map(item => item.id === itemId ? { ...item, quantity } : item)
    );
  }

  clearCart(): void {
    this.cartItemsSignal.set([]);
  }
}
