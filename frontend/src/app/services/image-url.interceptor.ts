import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpResponse
} from '@angular/common/http';
import { Observable, map } from 'rxjs';

@Injectable()
export class ImageUrlInterceptor implements HttpInterceptor {
  private baseApiUrl = 'http://localhost:8080';
  private localPlaceholder = 'assets/images/product-placeholder.svg';

  // Daha önce başarısız olan URL'leri takip etmek için
  private failedImageUrls: Set<string> = new Set();

  constructor() {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      map(event => {
        // Sadece HttpResponse'ları işliyoruz
        if (event instanceof HttpResponse) {
          // API'den gelen ürünlerin görsel URL'lerini kontrol edip düzeltelim
          const body = event.body;

          if (body) {
            // Eğer body bir dizi ise veya products dizisi içeriyorsa
            if (Array.isArray(body)) {
              this.processProductList(body);
            } else if (body.products && Array.isArray(body.products)) {
              this.processProductList(body.products);
            } else if (body.content && Array.isArray(body.content)) {
              this.processProductList(body.content);
            } else if (body.imageUrl) {
              // Tek bir ürün nesnesi ise
              this.processImageUrl(body);
            }
          }

          return event.clone({ body });
        }
        return event;
      })
    );
  }

  // Ürün listesindeki tüm ürünlerin görsel URL'lerini işler
  private processProductList(products: any[]): void {
    products.forEach(product => {
      this.processImageUrl(product);
    });
  }

  // Bir ürünün görsel URL'sini kontrol eder ve gerekirse düzeltir
  private processImageUrl(product: any): void {
    // Eğer ürünün görsel URL'si yoksa veya daha önce başarısız olduysa placeholder kullan
    if (!product.imageUrl || this.failedImageUrls.has(product.imageUrl)) {
      product.imageUrl = this.localPlaceholder;
      return;
    }

    // Eğer URL geçerli bir HTTP URL değilse veya göreceli bir yolsa
    if (!product.imageUrl.startsWith('http') && !product.imageUrl.startsWith('data:')) {
      // Eğer URL bir forward slash ile başlıyorsa
      if (product.imageUrl.startsWith('/')) {
        product.imageUrl = `${this.baseApiUrl}${product.imageUrl}`;
      } else {
        product.imageUrl = `${this.baseApiUrl}/${product.imageUrl}`;
      }
    }
  }

  // Başarısız görsel URL'lerini kaydet
  public markImageUrlAsFailed(url: string): void {
    this.failedImageUrls.add(url);
  }
}
