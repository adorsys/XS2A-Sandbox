import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RoutingPath } from '../common/models/routing-path.model';
import { NotFoundComponent } from '../not-found/not-found.component';
import { AuthorizeComponent } from './authorize/authorize.component';

const routes: Routes = [
  {
    path: RoutingPath.AUTHORIZE,
    component: AuthorizeComponent,
  },
  {
    path: '**',
    component: NotFoundComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AuthRoutingModule {}
