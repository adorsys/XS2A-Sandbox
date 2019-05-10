import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {
  currentRouteUrl = '';

  constructor(private router: Router, private actRoute: ActivatedRoute) {}

  goToPage(page) {
    this.router.navigateByUrl(`/${page}`);
  }

  onActivate(ev) {
    this.currentRouteUrl = this.actRoute['_routerState'].snapshot.url;
  }

  ngOnInit() {}
}
