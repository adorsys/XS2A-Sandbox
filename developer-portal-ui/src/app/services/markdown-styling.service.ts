import {Injectable} from '@angular/core';
import {MarkdownService} from 'ngx-markdown';

@Injectable({
  providedIn: 'root',
})
export class MarkdownStylingService {

  private counter = 1;

  constructor(private markdownService: MarkdownService) {
  }

  public resetCounter() {
    this.counter = 0;
  }

  public createTableOfContent(contentTable?: boolean) {
    this.markdownService.renderer.heading = (text: string, level: number) => {
      const headerTag = `<section id="${this.counter}"><h${level}> ${text} </h${level}></section>`;

      if (level == 1) {
        if (contentTable) {
          this.addHeaderToTable(text, this.counter);
        }
      }
      this.counter++;

      return headerTag;
    };
  }

  private addHeaderToTable(text: string, id: number) {
    const li = document.createElement('li');
    li.innerHTML = `<a href="/getting-started#${id}">${text}</a>`;

    const ol = document.getElementById('contentTable');
    if (ol) {
      ol.appendChild(li);
    }
  }
}
