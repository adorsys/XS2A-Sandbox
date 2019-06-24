import { NgModule } from '@angular/core';
import { Routes, RouterModule, ExtraOptions } from '@angular/router';
import { HomeComponent } from '../pages/home/home.component';
import { GettingStartedComponent } from '../pages/getting-started/getting-started.component';
import { FaqComponent } from '../pages/faq/faq.component';
import { ContactComponent } from '../pages/contact/contact.component';
import { AdminComponent } from '../pages/admin/admin.component';

const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'getting-started', component: GettingStartedComponent },
  { path: 'faq', component: FaqComponent },
  { path: 'contact', component: ContactComponent },
  { path: 'admin', component: AdminComponent },
  { path: '**', component: HomeComponent },
];
const routerOptions: ExtraOptions = {
  scrollPositionRestoration: 'top',
};

@NgModule({
  imports: [RouterModule.forRoot(routes, routerOptions)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
