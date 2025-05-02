import { Product } from './product.model';

export interface OrderItem {
  id: number;
  orderId: number;
  productId: number;
  productName: string;
  quantity: number;
  price: number;
  subTotal: number;
  imageUrl?: string;
}