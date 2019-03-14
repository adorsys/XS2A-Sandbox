import { NgModule } from '@angular/core';
import { MarkdownModule } from 'ngx-markdown';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { routes } from './cert-service.routing';
import { FormsModule } from '@angular/forms';
import { CreateCertPageComponent } from './create-cert-page/create-cert-page.component';
import { MaxValidatorDirective } from '../common/validators/max-validator.directive';
import { MinValidatorDirective } from '../common/validators/min-validator.directive';
import { SharedModule } from '../common/shared.module';

@NgModule({
  imports: [
    CommonModule,
    MarkdownModule.forChild(),
    RouterModule.forChild(routes),
    FormsModule,
    SharedModule,
  ],
  declarations: [
    CreateCertPageComponent,
    MaxValidatorDirective,
    MinValidatorDirective,
  ],
})
export class CertServiceModule {}
