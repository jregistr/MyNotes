import {Component, EventEmitter, Input, OnChanges, OnInit} from '@angular/core';
import {TodoListsService} from "../../../services/todo-lists.service";
import {Observable} from 'rxjs/Rx';
import {TodoList} from "../../../models/models";
import {EventEmitterService} from "../../../services/event-emitter.service";


@Component({
  selector: 'app-todo-gallery',
  templateUrl: './todo-gallery.component.html',
  styleUrls: ['./todo-gallery.component.sass']
})
export class TodoGalleryComponent implements OnInit {

  todoLists: TodoList[] = [];
  addMode: boolean = false;

  constructor(private service: TodoListsService) {
    this.loadTodoLists();

    EventEmitterService.get("Create-Todo-Done").subscribe((sent: boolean) => {
      this.addMode = false;
    });

    EventEmitterService.get("new-todo-came").subscribe((todoList: TodoList) => {
      this.todoLists.push(todoList);
    });

    EventEmitterService.get("delete-todo-list").subscribe((todoList: TodoList) => {
      service.deleteTodoList(todoList).subscribe(() => {
        this.loadTodoLists();
      });
    });

  }

  ngOnInit() {

  }

  onAddToListsClicked(): void {
    this.addMode = true;
  }

  private loadTodoLists() {
    this.service.getTodoLists().subscribe(
      todoLists => this.todoLists = todoLists,
      error2 => {
        console.log(error2);
      }
    );
  }

}
