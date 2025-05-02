import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatBadgeModule } from '@angular/material/badge';
import { MatDividerModule } from '@angular/material/divider';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { User } from '../../models/user.model';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatBadgeModule,
    MatDividerModule
  ],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  isLoggedIn = false;
  isAdmin = false;
  currentUser: User | null = null;
  cartItemCount = 0;
  private cartSubscription?: Subscription;

  constructor(
    private authService: AuthService,
    private cartService: CartService,
    private router: Router
  ) { }

  ngOnInit(): void {
    // Kullanıcı giriş durumunu izle
    this.authService.currentUser$.subscribe((user: User | null) => {
      this.isLoggedIn = !!user;
      this.currentUser = user;
      this.isAdmin = this.authService.isAdmin();

      if (this.isLoggedIn) {
        // Sepet öğe sayısını izle
        this.cartSubscription = this.cartService.cartItemCount$.subscribe((count: number) => {
          this.cartItemCount = count;
        });
      }
    });
  }

  ngOnDestroy(): void {
    // Abonelikten çıkarak bellek sızıntısını önle
    if (this.cartSubscription) {
      this.cartSubscription.unsubscribe();
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}