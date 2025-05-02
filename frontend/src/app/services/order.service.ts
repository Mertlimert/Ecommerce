import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order } from '../models/order.model';

const API_URL = 'http://localhost:8080/api/orders';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  constructor(private http: HttpClient) { }

  // Kullanıcının tüm siparişlerini getir
  getUserOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${API_URL}/user`);
  }

  // Belirli bir sipariş detayını getir
  getOrderById(orderId: number): Observable<Order> {
    return this.http.get<Order>(`${API_URL}/${orderId}`);
  }

  // Sepetten yeni sipariş oluştur
  createOrder(shippingAddress: string, paymentMethod: string): Observable<Order> {
    return this.http.post<Order>(`${API_URL}`, { shippingAddress, paymentMethod });
  }

  // Sipariş durumunu güncelle (admin için)
  updateOrderStatus(orderId: number, status: string): Observable<Order> {
    return this.http.patch<Order>(`${API_URL}/${orderId}/status`, { status });
  }

  // Siparişi iptal et
  cancelOrder(orderId: number): Observable<any> {
    return this.http.patch<any>(`${API_URL}/${orderId}/cancel`, {});
  }

  // Tüm siparişleri getir (admin için)
  getAllOrders(page: number = 0, size: number = 10): Observable<any> {
    return this.http.get<any>(`${API_URL}/admin?page=${page}&size=${size}`);
  }

  // Sipariş tarihçesini takip et
  getOrderHistory(orderId: number): Observable<any[]> {
    return this.http.get<any[]>(`${API_URL}/${orderId}/history`);
  }
}