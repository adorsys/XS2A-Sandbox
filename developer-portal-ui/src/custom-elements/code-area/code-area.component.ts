import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-code-area',
  templateUrl: './code-area.component.html',
  styleUrls: ['./code-area.component.scss'],
})
export class CodeAreaComponent {
  @Input() json: object;
  @Input() id: string;
  shown = false;

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

  copyText(json: object) {
    let textToCopy = JSON.stringify(json);
    let selBox = document.createElement('textarea');
    selBox.style.position = 'fixed';
    selBox.style.left = '0';
    selBox.style.top = '0';
    selBox.style.opacity = '0';
    selBox.value = textToCopy;
    document.body.appendChild(selBox);
    selBox.focus();
    selBox.select();
    document.execCommand('copy');
    document.body.removeChild(selBox);
    alert('Body copied to clipboard!');
  }
}
