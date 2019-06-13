import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DataService } from '../../services/data.service';

@Component({
  selector: 'app-test-cases',
  templateUrl: './test-cases.component.html',
  styleUrls: ['./test-cases.component.scss'],
})
export class TestCasesComponent implements OnInit {
  redirectFlag = false;
  embeddedFlag = false;
  accountFlag = false;

  constructor(
    private router: Router,
    public dataService: DataService,
    private actRoute: ActivatedRoute
  ) {}

  onActivate(ev) {
    this.dataService.currentRouteUrl = this.actRoute[
      '_routerState'
    ].snapshot.url;
  }

  collapseThis(collapseId: string): void {
    const collapsibleItemContent = document.getElementById(
      `${collapseId}-content`
    );

    switch (collapseId) {
      case 'redirect':
        this.redirectFlag = !this.redirectFlag;
        break;
      case 'embedded':
        this.embeddedFlag = !this.embeddedFlag;
        break;
      case 'account':
        this.accountFlag = !this.accountFlag;
        break;
    }

    if (collapsibleItemContent.style.maxHeight) {
      collapsibleItemContent.style.maxHeight = '';
    } else {
      collapsibleItemContent.style.maxHeight = `${
        collapsibleItemContent.scrollHeight
      }px`;
    }
  }

  ngOnInit() {
    this.collapseThis('redirect');
  }
}
