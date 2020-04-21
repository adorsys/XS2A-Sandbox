import { NgModule } from '@angular/core';
import { Routes, RouterModule, ExtraOptions } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { GettingStartedComponent } from './components/getting-started/getting-started.component';
import { ContactComponent } from './components/contact/contact.component';
import { CustomPageComponent } from './components/custom-page/custom-page.component';

const routes: Routes = [
  {
    path: 'home',
    component: HomeComponent,
  },
  {
    path: 'getting-started',
    component: GettingStartedComponent,
  },
  {
    path: 'contact',
    component: ContactComponent,
  },
  {
    path: 'page/:name',
    component: CustomPageComponent,
  },
  {
    path: 'test-cases',
    loadChildren: './components/test-cases/test-cases.module#TestCasesModule',
  },
  {
    path: '**',
    component: HomeComponent,
  },
];
const routerOptions: ExtraOptions = {
  scrollPositionRestoration: 'top',
  anchorScrolling: 'enabled',
};

@NgModule({
  imports: [RouterModule.forRoot(routes, routerOptions)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
