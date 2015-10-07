# Getting Started #

The first think we need to do is to create our graph. At the moment of this writing there are 2 underlying implementations of the Graph interface: the _AdjacencyMatrixGraph_ (good for dense graphs) and the _AdjacencyMapGraph_ (good for sparse graphs).

For this example, we choose the _AdjacencyMapGraph_ :

```
Graph friendGraph = GraphFactory.instantiateAdjacencyMapGraph();
```

Next we will want to add some nodes as in the following image:

TODO: Add image of graph layout.

Adding nodes is as simple as:

```
Node me    = friendGraph.addNode(new Friend("Myself"));
Node bender= friendGraph.addNode(new Friend("Bender"));
Node peter = friendGraph.addNode(new Friend("Peter"));
Node homer = friendGraph.addNode(new Friend("Homer"));
Node stan  = friendGraph.addNode(new Friend("Stan"));
```

You might noticed that when adding a node we also assign a content to it, in this case a _Friend_ object. This content is required to implement the interface _Measurable_. We will talk more about it later.

The next step would be to add some connections between our friends. All from this persons, all but Stan are our friends, so lets start with our connections:

```
me.addArc(bender, new YearsBeeingFriends(3));
me.addArc(peter, new YearsBeeingFriends(1));
me.addArc(homer, new YearsBeeingFriends(12));
```

Once again notice that when adding an arc we assign an content to it, in this case a _YearsBeeingFriends_ object. Once again this object implements _Measurable_.

Lets add some more relations between our friends:

```
bender.addArc(homer, new YearsBeeingFriends(7));
stan.addArc(peter, new YearsBeeingFriends(1));
```

In the previous arcs we added, we didn't specify any direction, Lets suppose homer secretly hates peter, but peter thinks homer is a good guy.

Lets add this relationship:

```
peter.addArcTo(homer, new YearsBeeingFriends(1));
```

Ok, now that we got our graph set up, we can ask our self if homer considers bender a friend:

```
boolean homerConsidersBenderAFriend = homer.isDirectionallyConnectedTo(bender);
```

Or if both just are mutual friends:


```
boolean homerAndBenderAreFriends = homer.isConnectedTo(bender);
```

Now we are really exited with the graph library ;), and want to throw a party. Now the think is to invite a friend of friend we must be sure our friend is coming or it would be too awkward. So we need a list of friends to call with phone where our friends are on the top of the list, then the friends of our friends and so on... This assures us we know a friend of us is coming, so his friends can come too. Now to solve this we just have to apply a Breath First Traversal on our graph, starting from our node:

```
List<Node> phoneList = friendGraph.breathFirstTraversal(me);
```

That ends this small starting guide. Please feel free to add a comment if you would like to see an addition.