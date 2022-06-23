import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-intro-component',
  templateUrl: './intro.component.html',
  styleUrls: ['./intro.component.scss'],
})
export class IntroComponent implements OnInit {
  @Input() redirectSupported: boolean;

  @Input() embeddedSupported: boolean;

  constructor() {}

  ngOnInit(): void {}
}
