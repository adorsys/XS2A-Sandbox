/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at psd2@adorsys.com.
 */

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FileUploader } from 'ng2-file-upload';
import { FileItem } from 'ng2-file-upload/file-upload/file-item.class';
import { ParsedResponseHeaders } from 'ng2-file-upload/file-upload/file-uploader.class';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class UploadService {
  constructor(protected http: HttpClient, private auth: AuthService) {}

  createInstanceFileUploader(options: UploadOptions): FileUploader {
    // Set the current user credentials in Headers for Basic Authorization
    const headers = [];
    const header = {
      name: 'Authorization',
      value: 'Bearer '.concat(this.auth.getAuthorizationToken()),
    };
    headers.push(header);

    return new FileUploader({
      url: options.url,
      method: options.method,
      headers,
      allowedFileType: options.allowedMimeType,
    });
  }
}

export interface UploadOptions {
  title: string;
  exampleFileName?: string;
  method: string;
  url: string;
  queueLimit?: number;
  allowedMimeType?: string[];
  exampleFileUrl?: string;

  methodAfterSuccess?(
    item?: FileItem,
    response?: string,
    status?: number,
    headers?: ParsedResponseHeaders
  ): any;
}
