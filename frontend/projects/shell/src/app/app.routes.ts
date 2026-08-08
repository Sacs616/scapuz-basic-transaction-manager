import { Routes } from '@angular/router';
import { loadRemoteModule } from '@angular-architects/native-federation';

export const routes: Routes = [
  {
    path: 'clientes',
    loadComponent: () =>
      loadRemoteModule('mfe-clientes', './Component').then((m) => m.AppComponent),
  },
  {
    path: 'cuentas',
    loadComponent: () =>
      loadRemoteModule('mfe-cuentas', './Component').then((m) => m.AppComponent),
  },
  {
    path: 'movimientos',
    loadComponent: () =>
      loadRemoteModule('mfe-movimientos', './Component').then((m) => m.AppComponent),
  },
  {
    path: 'reportes',
    loadComponent: () =>
      loadRemoteModule('mfe-reportes', './Component').then((m) => m.AppComponent),
  },
  {
    path: '',
    redirectTo: '/clientes',
    pathMatch: 'full',
  },
];
