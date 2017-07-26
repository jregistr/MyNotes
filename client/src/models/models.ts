export class TodoList {
  constructor(public userId: number,
              public name: string,
              description: string,
              public items: Item[],
              id: number) {
  }
}

export class Item {

  constructor(
    public todoListId: number,
    public text: string,
    public checked: boolean,
    public id: number) {
  }
}

export class User {

  constructor(
    public email: String
    ,
    public password: String
    ,
    public firstName: String
    ,
    public lastName: string
    ,
    public id: number
  ) {
  }
}

