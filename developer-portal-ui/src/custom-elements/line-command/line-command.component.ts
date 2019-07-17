import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-line-command',
  templateUrl: './line-command.component.html',
  styleUrls: ['./line-command.component.scss'],
})
export class LineCommandComponent {
  @Input() title: string;
}
