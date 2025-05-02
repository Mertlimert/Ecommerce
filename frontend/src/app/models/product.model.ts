import { Category } from './category.model';

export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  stockQuantity: number;
  imageUrl?: string;
  categoryId: number;
  categoryName?: string;
  discountPercent?: number;
  rating?: number;
  createdAt?: Date;
  updatedAt?: Date;
}
