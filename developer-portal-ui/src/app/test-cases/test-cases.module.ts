import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { routes } from './test-cases.routing';
import { FormsModule } from '@angular/forms';
import { TestCasesPageComponent } from './test-cases-page/test-cases-page.component';
import { SharedModule } from '../common/shared.module';
import { MarkdownModule } from 'ngx-markdown';

@NgModule({
  imports: [
    CommonModule,
    MarkdownModule.forChild(),
    RouterModule.forChild(routes),
    FormsModule,
    SharedModule,
  ],
  declarations: [TestCasesPageComponent],
})
export class TestCasesModule {}
