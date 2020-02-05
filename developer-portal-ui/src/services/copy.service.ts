import { Injectable } from '@angular/core';
import { pathOr } from 'ramda';
import { DataService } from './data.service';

@Injectable({ providedIn: 'root' })
export class CopyService {
  constructor(private dataService: DataService) {}

  copyThis(index: number, fieldsToCopy: string[], fieldName?: string) {
    const copyText = document.getElementById(`input-${index}`);
    this.copyTextToClipboard(copyText['value'], index, fieldsToCopy, fieldName);
  }

  copyTextToClipboard(
    text: string,
    index: number,
    fieldsToCopy: string[],
    fieldName?: string
  ) {
    if (!navigator.clipboard) {
      this.fallbackCopyTextToClipboard(text, index, fieldsToCopy);
      return;
    }

    const name = fieldName ? fieldName : fieldsToCopy[index];
    navigator.clipboard.writeText(text).then(
      () => {
        this.dataService.showToast(
          `${name} copied`,
          'Copy success!',
          'success'
        );
      },
      err => {
        console.error('Async: Could not copy text: ', err);
      }
    );
  }

  fallbackCopyTextToClipboard(
    text: string,
    index: number,
    fieldsToCopy: string[]
  ) {
    const textArea = document.createElement('textarea');
    textArea.value = text;
    document.body.appendChild(textArea);
    textArea.focus();
    textArea.select();

    try {
      const successful = document.execCommand('copy');
      const msg = successful ? 'successful' : 'unsuccessful';
      console.log('Fallback: Copying text command was ' + msg);
      this.dataService.showToast(
        `${fieldsToCopy[index]} copied`,
        'Copy success!',
        'success'
      );
    } catch (err) {
      console.error('Fallback: Oops, unable to copy', err);
    }

    document.body.removeChild(textArea);
  }

  getCopyValue(
    i: number,
    fieldsToCopy: string[],
    response: any,
    paymentId: string
  ) {
    if (fieldsToCopy[i] === 'authorisationId') {
      const hrefPath = pathOr(
        null,
        ['body', '_links', 'scaStatus', 'href'],
        response
      );
      if (hrefPath) {
        return hrefPath.split(['/authorisations/'])[1];
      }
    }

    let r = pathOr('', ['body', fieldsToCopy[i]], response);
    if (r === '') {
      const h = pathOr(
        null,
        ['body', '_links', 'updatePsuAuthentication', 'href'],
        response
      );
      if (h) {
        r = this._getLinkParam(h, i);
      } else if (i === 1) {
        r = paymentId;
      }
    }

    return r;
  }

  /**
   * @param h - link
   * @param i - index
   */
  private _getLinkParam(h, i) {
    const linkParts = h.split('/');
    if (i === 0) {
      return linkParts[linkParts.length - 1];
    } else {
      return linkParts[linkParts.length - 3];
    }
  }
}
