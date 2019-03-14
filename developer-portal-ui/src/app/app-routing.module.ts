import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FeatureEnabledGuard } from './common/FeatureEnabledGuard';

const routes: Routes = [
  { path: '', redirectTo: 'developer-portal', pathMatch: 'full' },
  {
    path: 'certificate-service',
    loadChildren: './cert-service/cert-service.module#CertServiceModule',
    canActivate: [FeatureEnabledGuard],
  },
  {
    path: 'developer-portal',
    loadChildren: './dev-portal/dev-portal.module#DevPortalModule',
  },
  {
    path: 'faqs',
    loadChildren: './faqs/faqs.module#FaqsModule',
  },
  {
    path: 'test-cases',
    loadChildren: './test-cases/test-cases.module#TestCasesModule',
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
