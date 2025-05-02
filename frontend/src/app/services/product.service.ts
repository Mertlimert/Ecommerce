import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Product } from '../models/product.model';

const API_URL = 'http://localhost:8080/api/products';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  constructor(private http: HttpClient) {}

  // Tüm ürünleri getir
  getAllProducts(): Observable<Product[]> {
    return this.http.get<any>(API_URL).pipe(
      map(response => {
        // API yanıtını kontrol et ve log'la
        console.log('API response structure:', response);
        
        // Eğer 'content' içinde bir array varsa, bu pageable response demektir
        if (response && response.products) {
          return response.products;
        }
        
        // Eğer yanıt direkt ürün dizisi ise
        if (Array.isArray(response)) {
          return response;
        }
        
        // Eğer 'content' içinde bir array varsa
        if (response && response.content && Array.isArray(response.content)) {
          return response.content;
        }
        
        // Hiçbiri uymazsa boş array döndür
        console.error('Unexpected API response format:', response);
        return [];
      })
    );
  }

  // Sayfalama ve filtreleme ile ürünleri getir
  getProducts(page: number = 0, size: number = 10, sort: string = 'id,asc', categoryId?: number): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    if (categoryId) {
      params = params.set('categoryId', categoryId.toString());
    }

    return this.http.get<any>(`${API_URL}`, { params });
  }

  // Belirli bir ürünü getir
  getProduct(id: number): Observable<Product> {
    return this.http.get<Product>(`${API_URL}/${id}`);
  }

  // Ürün ara
  searchProducts(query: string, page: number = 0, size: number = 10): Observable<any> {
    const params = new HttpParams()
      .set('query', query)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<any>(`${API_URL}/search`, { params });
  }

  // Öne çıkan ürünleri getir
  getFeaturedProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${API_URL}/featured`);
  }

  // Yeni ürünleri getir
  getNewArrivals(): Observable<Product[]> {
    return this.http.get<Product[]>(`${API_URL}/new-arrivals`);
  }

  // İndirimli ürünleri getir
  getDiscountedProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${API_URL}/discounted`);
  }

  // Ürün ekle (Admin)
  addProduct(product: Product): Observable<Product> {
    return this.http.post<Product>(API_URL, product);
  }

  // Ürün güncelle (Admin)
  updateProduct(id: number, product: Product): Observable<Product> {
    return this.http.put<Product>(`${API_URL}/${id}`, product);
  }

  // Ürün sil (Admin)
  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${id}`);
  }

  // Ürün resmi yükle (Admin)
  uploadProductImage(id: number, image: File): Observable<any> {
    const formData = new FormData();
    formData.append('image', image);
    return this.http.post<any>(`${API_URL}/${id}/image`, formData);
  }

  // En çok satan ürünleri getir
  getBestSellers(): Observable<Product[]> {
    return this.http.get<Product[]>(`${API_URL}/best-sellers`);
  }

  // İlgili ürünleri getir
  getRelatedProducts(id: number): Observable<Product[]> {
    return this.http.get<Product[]>(`${API_URL}/${id}/related`);
  }
}
