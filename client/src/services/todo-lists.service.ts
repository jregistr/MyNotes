import {Injectable} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {TodoList} from '../models/models';
import {Observable} from 'rxjs/Rx';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';

@Injectable()
export class TodoListsService {

  private http: Http;

  constructor(http: Http) {
    this.http = http;
  }

  getTodoLists(): Observable<TodoList[]> {
    const params = new URLSearchParams();
    params.set("userId", "1");
    return this.http.get("/api/todolist", {params: {userId: 1}})
      .map(res => res.json().data)
      .catch(error => Observable.throw("Server Err"))
  }

}
