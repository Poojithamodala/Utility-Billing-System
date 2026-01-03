import { Routes } from '@angular/router';
import { Login } from './components/login/login';
import { ActivateAccount } from './components/activate-account/activate-account';
import { AppLayout } from './layout/app-layout/app-layout';

export const routes: Routes = [
    { path: 'login', component: Login },
    { path: 'activate', component: ActivateAccount },
    {
        path: '',
        component: AppLayout,
        children: [
            // { path: 'admin', loadComponent: () => import('./admin/admin.component') },
            // { path: 'billing', loadComponent: () => import('./billing/billing.component') },
            // { path: 'accounts', loadComponent: () => import('./accounts/accounts.component') },
            // { path: 'consumer', loadComponent: () => import('./consumer/consumer.component') }
        ]
    }
];
