import { HttpError } from '../../models/httpError';

export class ErrorHandler {
  public static getErrorMessages(error: any): Array<HttpError> {
    const errors: HttpError[] = [];
    let httpError: HttpError;
    if (error.status === 504) {
      httpError = {
        subject: 'Gateway Timeout',
        message:
          'The server is currently not available. Please try again later.',
      };
      errors.push(httpError);
    } else {
      error.error.forEach(errorElement => {
        httpError = { subject: errorElement.code, message: errorElement.text };
        errors.push(httpError);
      });
    }
    return errors;
  }
}
