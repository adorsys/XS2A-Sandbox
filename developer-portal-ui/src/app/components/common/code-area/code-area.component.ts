import {Component, Input} from '@angular/core';
import {DataService} from "../../../services/data.service";

@Component({
  selector: 'app-code-area',
  templateUrl: './code-area.component.html',
  styleUrls: ['./code-area.component.scss'],
})
export class CodeAreaComponent {
  @Input() json: object;
  @Input() id: string;
  shown = false;

  constructor(private dataService: DataService) {
  }

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
    const textToCopy = JSON.stringify(json, null, 4);
    const selBox = document.createElement('textarea');
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
    this.dataService.showToast(
      'Json body is copied to clipboard.',
      'Copied successfully!',
      'success'
    );
  }
}
