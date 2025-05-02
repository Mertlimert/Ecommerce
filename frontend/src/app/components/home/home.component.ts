import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatGridListModule } from '@angular/material/grid-list';
import { Product } from '../../models/product.model';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatGridListModule
  ],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  featuredProducts: Product[] = [];
  loading = true;
  errorMessage = '';

  constructor(
    private productService: ProductService,
    private cartService: CartService
  ) { }

  ngOnInit(): void {
    this.loadFeaturedProducts();
  }

  loadFeaturedProducts(): void {
    this.loading = true;
    this.errorMessage = '';

    this.productService.getAllProducts().subscribe({
      next: (products) => {
        console.log('Products loaded (count):', products?.length);
        console.log('Products data:', JSON.stringify(products));
        this.featuredProducts = products;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading products:', err);
        this.errorMessage = 'Ürünler yüklenirken bir hata oluştu.';
        this.loading = false;
      }
    });
  }

  /**
   * Görsel yüklenirken hata oluşursa yedek görseli kullan
   */
  handleImageError(event: Event): void {
    const imgElement = event.target as HTMLImageElement;
    imgElement.src = 'assets/images/product-placeholder.svg';
    imgElement.onerror = null; // Sonsuz döngüyü önlemek için
  }

  /**
   * Ürünü sepete ekle
   */
  addToCart(product: Product): void {
    this.cartService.addToCart(product);
  }
}
