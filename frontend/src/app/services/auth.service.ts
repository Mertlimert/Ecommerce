import { Injectable, PLATFORM_ID, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { User } from '../models/user.model';
import { Router } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';

const API_URL = 'http://localhost:8080/api/auth';
const TOKEN_KEY = 'auth-token';
const USER_KEY = 'auth-user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser$: Observable<User | null>;
  private isBrowser: boolean;

  constructor(
    private http: HttpClient,
    private router: Router,
    @Inject(PLATFORM_ID) platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(platformId);
    this.currentUserSubject = new BehaviorSubject<User | null>(this.getUserFromStorage());
    this.currentUser$ = this.currentUserSubject.asObservable();
  }

  // Kullanıcı girişi
  login(username: string, password: string): Observable<any> {
    return this.http.post<any>(`${API_URL}/signin`, { username, password })
      .pipe(
        tap(response => {
          if (response && response.token && this.isBrowser) {
            // Tokeni sakla
            localStorage.setItem(TOKEN_KEY, response.token);
            // Kullanıcı bilgisini sakla
            localStorage.setItem(USER_KEY, JSON.stringify(response.user));
            // Mevcut kullanıcı bilgisini güncelle
            this.currentUserSubject.next(response.user);
          }
        })
      );
  }

  // Kullanıcı kaydı
  register(user: any): Observable<any> {
    return this.http.post<any>(`${API_URL}/register`, user);
  }

  // Çıkış yap
  logout(): void {
    // Yerel depodan tokeni ve kullanıcı bilgisini temizle
    if (this.isBrowser) {
      localStorage.removeItem(TOKEN_KEY);
      localStorage.removeItem(USER_KEY);
    }

    // Mevcut kullanıcı değerini null yap
    this.currentUserSubject.next(null);

    // Ana sayfaya yönlendir
    this.router.navigate(['/']);
  }

  // Kullanıcı giriş yapmış mı kontrol et
  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  // Tokeni getir
  getToken(): string | null {
    if (!this.isBrowser) {
      return null;
    }
    return localStorage.getItem(TOKEN_KEY);
  }

  // Kullanıcı bilgisini getir
  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  // Yerel depodan kullanıcı bilgisini getir
  private getUserFromStorage(): User | null {
    if (!this.isBrowser) {
      return null;
    }
    const user = localStorage.getItem(USER_KEY);
    return user ? JSON.parse(user) : null;
  }

  // Kullanıcı rolünü kontrol et
  hasRole(role: string): boolean {
    const user = this.getCurrentUser();
    return user !== null && user.roles && user.roles.includes(role);
  }

  // Kullanıcı admin mi kontrol et
  isAdmin(): boolean {
    return this.hasRole('ROLE_ADMIN');
  }

  // Şifre sıfırlama e-postası gönder
  forgotPassword(email: string): Observable<any> {
    return this.http.post<any>(`${API_URL}/forgot-password`, { email });
  }

  // Şifre sıfırlama
  resetPassword(token: string, newPassword: string): Observable<any> {
    return this.http.post<any>(`${API_URL}/reset-password`, { token, newPassword });
  }

  // Kullanıcı bilgilerini güncelle
  updateProfile(user: User): Observable<User> {
    return this.http.put<User>(`${API_URL}/profile`, user)
      .pipe(
        tap(updatedUser => {
          if (this.isBrowser) {
            // Güncellenmiş kullanıcı bilgisini yerel depoda sakla
            localStorage.setItem(USER_KEY, JSON.stringify(updatedUser));
          }
          // Mevcut kullanıcı bilgisini güncelle
          this.currentUserSubject.next(updatedUser);
        })
      );
  }

  // Şifre değiştir
  changePassword(oldPassword: string, newPassword: string): Observable<any> {
    return this.http.post<any>(`${API_URL}/change-password`, { oldPassword, newPassword });
  }
}
