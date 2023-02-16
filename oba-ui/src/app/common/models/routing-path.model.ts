/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
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

export enum RoutingPath {
  ACCOUNT_INFORMATION = 'account-information',
  PAYMENT_INITIATION = 'payment-initiation',
  PAYMENT_CANCELLATION = 'payment-cancellation',
  LOGIN = 'login',
  AUTHORIZE = 'authorize',
  TOKEN = 'token',
  ACCOUNTS = 'accounts',
  CONSENTS = 'consents',
  RESULT = 'result',
  BANK_OFFERED = 'bank-offered',
  GRANT_CONSENT = 'grant-consent',
  CONFIRM_PAYMENT = 'confirm-payment',
  CONFIRM_CANCELLATION = 'confirm-cancellation',
  SELECT_SCA = 'select-sca',
  TAN_CONFIRMATION = 'tan-confirm',
  ERROR_404 = 'tan-confirm',
}
