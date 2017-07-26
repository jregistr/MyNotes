import {Component, EventEmitter, Input, OnChanges, OnInit} from '@angular/core';
import {TodoListsService} from "../../../services/todo-lists.service";
import {Observable} from 'rxjs/Rx';
import {TodoList} from "../../../models/models";


@Component({
  selector: 'app-todo-gallery',
  templateUrl: './todo-gallery.component.html',
  styleUrls: ['./todo-gallery.component.sass']
})
export class TodoGalleryComponent implements OnInit {

  private todoLists: TodoList[] = [];

  constructor(private service: TodoListsService) {
    this.service.getTodoLists().subscribe(
      todoLists => this.todoLists = todoLists,
      error2 => {
        console.log(error2);
      }
    );
  }

  ngOnInit() {
  }

}
