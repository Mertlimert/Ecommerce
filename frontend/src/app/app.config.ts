import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { provideHttpClient, withFetch, HTTP_INTERCEPTORS } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';

import { routes } from './app.routes';
import { AuthInterceptor, authInterceptorProviders } from './services/auth.interceptor';
import { ImageUrlInterceptor } from './services/image-url.interceptor';

// ImageUrlInterceptor provider
export const imageUrlInterceptorProviders = [
  { provide: HTTP_INTERCEPTORS, useClass: ImageUrlInterceptor, multi: true }
];

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient(withFetch()),
    authInterceptorProviders,
    imageUrlInterceptorProviders,
    provideAnimations()
  ]
};
