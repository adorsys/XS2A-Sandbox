import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-line-command',
  templateUrl: './line-command.component.html',
  styleUrls: ['./line-command.component.scss'],
})
export class LineCommandComponent implements OnInit {
  @Input() title: string;

  constructor() {}

  ngOnInit() {}
}
