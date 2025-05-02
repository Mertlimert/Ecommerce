import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { Cart } from '../models/cart.model';
import { CartItem } from '../models/cart-item.model';
import { Product } from '../models/product.model';
import { map, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private apiUrl = 'http://localhost:8080/api/cart';

  private cartSubject = new BehaviorSubject<Cart>({ items: [], total: 0 });
  public cart$ = this.cartSubject.asObservable();

  private cartItemCountSubject = new BehaviorSubject<number>(0);
  public cartItemCount$ = this.cartItemCountSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadCart();
  }

  private loadCart(): void {
    this.http.get<Cart>(`${this.apiUrl}`).subscribe({
      next: (cart) => {
        this.cartSubject.next(cart);
        this.cartItemCountSubject.next(this.calculateCartItemCount(cart));
      },
      error: (error) => {
        console.error('Error loading cart', error);
      }
    });
  }

  private calculateCartItemCount(cart: Cart): number {
    return cart.items.reduce((count, item) => count + item.quantity, 0);
  }

  addToCart(product: Product, quantity: number = 1): Observable<Cart> {
    const cartItem: CartItem = {
      productId: product.id,
      name: product.name,
      price: product.price,
      quantity: quantity,
      imageUrl: product.imageUrl
    };

    return this.http.post<Cart>(`${this.apiUrl}/add`, cartItem).pipe(
      tap(cart => {
        this.cartSubject.next(cart);
        this.cartItemCountSubject.next(this.calculateCartItemCount(cart));
      })
    );
  }

  removeFromCart(itemId: number): Observable<Cart> {
    return this.http.delete<Cart>(`${this.apiUrl}/remove/${itemId}`).pipe(
      tap(cart => {
        this.cartSubject.next(cart);
        this.cartItemCountSubject.next(this.calculateCartItemCount(cart));
      })
    );
  }

  updateCartItemQuantity(itemId: number, quantity: number): Observable<Cart> {
    return this.http.put<Cart>(`${this.apiUrl}/update/${itemId}`, { quantity }).pipe(
      tap(cart => {
        this.cartSubject.next(cart);
        this.cartItemCountSubject.next(this.calculateCartItemCount(cart));
      })
    );
  }

  clearCart(): Observable<Cart> {
    return this.http.delete<Cart>(`${this.apiUrl}/clear`).pipe(
      tap(cart => {
        this.cartSubject.next(cart);
        this.cartItemCountSubject.next(0);
      })
    );
  }

  getCartTotal(): Observable<number> {
    return this.cart$.pipe(
      map(cart => cart.items.reduce((total, item) => total + (item.price * item.quantity), 0))
    );
  }
}
