import {Injectable} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Item, TodoList} from '../models/models';
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
    return this.http.get("/api/todolist", {params: {userId: 1}})
      .map(res => res.json().data)
      .catch(error => Observable.throw("Server Err"))
  }

  getTodoListById(id: number): Observable<TodoList> {
    return this.http.get(`/api/todolist/${id}`)
      .map(res => res.json().data)
      .catch(error => Observable.throw("error getting todo by id"))
  }

  createTodoList(todoList: TodoList): Observable<TodoList> {
    const headers = new Headers();
    headers.append('Content-Type', 'application/x-www-form-urlencoded');
    const params = new URLSearchParams();
    params.append("userId", todoList.userId.toString());
    params.append("name", todoList.name);
    params.append("description", todoList.description);
    const body = params.toString();

    return this.http.post("/api/todolist/", body, {headers: headers})
      .map(res => res.json().data)
      .catch(err => Observable.throw("Erro creating new todolist"));
  }

  deleteTodoList(todoList: TodoList): Observable<boolean> {
    return this.http.delete(`/api/todolist/${todoList.id}`)
      .map(res => res.json().data)
      .catch(err => {
        console.error(err);
        return Observable.throw("Error with delete");
      })
  }

  deleteItem(item: Item): Observable<boolean> {
    return this.http.delete(`/api/items/${item.id}/ `)
      .map(res => res.json().data)
      .catch(err => {
        console.error(err);
        return Observable.throw("error deleting item");
      })
  }

  createItem(item: Item): Observable<Item> {
    const headers = new Headers();
    headers.append('Content-Type', 'application/x-www-form-urlencoded');
    const params = new URLSearchParams();
    params.append("todoListId", item.todoListId.toString());
    params.append("text", item.text);
    params.append("checked", item.checked.toString());
    const body = params.toString();

    return this.http.post("/api/items/", body, {headers: headers})
      .map(res => res.json().data)
      .catch(err => Observable.throw("Error creating item."))
  }

}
