import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-test-cases',
  templateUrl: './test-cases.component.html',
  styleUrls: ['./test-cases.component.scss'],
})
export class TestCasesComponent implements OnInit {
  constructor(private router: Router) {}

  navigateTo(component) {
    // this.router.navigate([component]);
  }

  ngOnInit() {}
}
