/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at psd2@adorsys.com.
 */

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
    loadChildren: () => import('./components/test-cases/test-cases.module').then((m) => m.TestCasesModule),
  },
  {
    path: '**',
    component: HomeComponent,
  },
];
const routerOptions: ExtraOptions = {
    scrollPositionRestoration: 'top',
    anchorScrolling: 'enabled'
};

@NgModule({
  imports: [RouterModule.forRoot(routes, routerOptions)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
