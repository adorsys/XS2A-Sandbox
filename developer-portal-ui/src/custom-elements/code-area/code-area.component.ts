import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-code-area',
  templateUrl: './code-area.component.html',
  styleUrls: ['./code-area.component.scss'],
})
export class CodeAreaComponent implements OnInit {
  @Input() json: object;
  @Input() id: string;
  shown = false;

  constructor() {}

  collapseThis(collapseId: string) {
    const collapsibleItemContent = document.getElementById(collapseId);
    if (collapseId === this.id) {
      this.shown = !this.shown;
    }
    if (collapsibleItemContent.style.maxHeight) {
      collapsibleItemContent.style.maxHeight = '';
    } else {
      collapsibleItemContent.style.maxHeight = `${
        collapsibleItemContent.scrollHeight
      }px`;
    }
  }

  ngOnInit() {}
}
