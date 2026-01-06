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
import { Dashboard } from './components/billing-officer/dashboard/dashboard';
import { PendingMeterReadings } from './components/billing-officer/pending-meter-readings/pending-meter-readings';
import { BillingOfficerBills } from './components/billing-officer/billing-officer-bills/billing-officer-bills';
import { AccountOfficerDashboard } from './components/account-officer/account-officer-dashboard/account-officer-dashboard';
import { AccountsPayments } from './components/account-officer/accounts-payments/accounts-payments';
import { OutstandingBills } from './components/account-officer/outstanding-bills/outstanding-bills';

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
    },
    {
        path: 'billing',
        component: AppLayout,
        children: [
            { path: 'home', component: Dashboard},
            { path: 'connection-requests', component: ConnectionRequests},
            { path: 'pending-meter-readings', component: PendingMeterReadings},
            { path: 'consumer-bills', component: BillingOfficerBills}
        ]
    },
    {
        path: 'accounts',
        component: AppLayout,
        children: [
            { path: 'home', component: AccountOfficerDashboard },
            { path: 'payments', component: AccountsPayments },
            { path: 'outstanding-bills', component: OutstandingBills}
        ]
    },

];
