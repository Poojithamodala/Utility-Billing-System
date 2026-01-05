import { Routes } from '@angular/router';
import { Home } from './components/home/home';
import { Login } from './components/login/login';
import { Register } from './components/register/register';
import { AppLayout } from './layout/app-layout/app-layout';
import { AdminHome } from './components/admin/admin-home/admin-home';
import { AdminRequests } from './components/admin/admin-requests/admin-requests';
import { ApprovedConsumers } from './components/admin/approved-consumers/approved-consumers';
import { ActivateAccount } from './components/activate-account/activate-account';
import { ConsumerHome } from './components/consumer/consumer-home/consumer-home';
import { ConsumerConnections } from './components/consumer/consumer-connections/consumer-connections';
import { ConsumerProfile } from './components/consumer/consumer-profile/consumer-profile';
import { ConsumerTariffs } from './components/consumer/consumer-tariffs/consumer-tariffs';
import { ConsumerBills } from './components/consumer/consumer-bills/consumer-bills';
import { ConsumerPayments } from './components/consumer/consumer-payments/consumer-payments';
import { ConnectionRequests } from './components/admin/connection-requests/connection-requests';

export const routes: Routes = [
    { path: '', component: Home },
    { path: 'login', component: Login },
    { path: 'register', component: Register },
    { path: 'activate', component: ActivateAccount },
    {
        path: 'admin',
        component: AppLayout,
        children: [
            { path: 'home', component: AdminHome },
            { path: 'requests', component: AdminRequests },
            { path: 'consumers', component: ApprovedConsumers},
            { path: 'connection-requests', component: ConnectionRequests}
        ]
    },
    {
        path: 'consumer',
        component: AppLayout,
        children: [
            { path: 'home', component: ConsumerHome },
            { path: 'connections', component: ConsumerConnections},
            { path: 'profile', component: ConsumerProfile },
            { path: 'tariffs', component: ConsumerTariffs },
            { path: 'bills', component: ConsumerBills },
            { path: 'payments', component: ConsumerPayments }
        ]
    }

];
