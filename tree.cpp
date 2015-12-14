/*
************************************************************************************************************************************
************************************************************************************************************************************
************************************************************************************************************************************
************************************************************************************************************************************
************************************************************************************************************************************
************************************************************************************************************************************
**       															  **
**       						PATH ORAM IMPLEMENTATION						  **
**       											written by Ashish/Anrin		  **
**       															  **
** This is the implementation of the PathORAM protocol along with recursive tree formation as described in the paper "https://eprint.iacr.org/2013/280.pdf"        												       **
**Here is the list of structs and functions that have been used in the code and one/two line explaining what they do              **
**       															  **
**       															  **
**       															  **
**       															  **
**       															  **
**       															  **
**       															  **
**       															  **
**       															  **
**       															  **
**       															  **
**       															  **
**       															  **
**       															  **
**       															  **
**       															  **
**       															  **
**       															  **
**       															  **
************************************************************************************************************************************
************************************************************************************************************************************
************************************************************************************************************************************
************************************************************************************************************************************
************************************************************************************************************************************
************************************************************************************************************************************
*/

#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<iostream>
#include<math.h>
#include<time.h>
#include<unordered_map>
#include <vector>


using namespace std;
unordered_map<int,int> leaf_map;
unordered_map<int,int> position_map;
unordered_map<int,int> recleaf_map;
unordered_map<int,int> recposition_map;
int leaf_count = 0;
int recleaf_count = 0;
float numblocks;
int data_value = 0 ;
int global_check = 0;
int count = 0;
vector<pair <int,int> > stash;
//vector<tnode> root_to_leaf_path_storage;



struct array_for_node{
	int array_node[4][2] = { {-1,-1}, {-1,-1}, {-1,-1}, {-1,-1} };
};

// This is the struct that holds the structure of the baisc tree which holds the blocks, as mentioned in the paper we have kept the bnumber of blocks that can be saved per node as 4...
typedef struct treenode
{
	int data;
	struct treenode* left;
	struct treenode* right;
	//struct listnode* listhead;
	int arr[4];
}tnode;

// This is the struct that holds the structure of the recursive trees that hold the position map info for four mappings.....
typedef struct recursive_tree_node
{
	int data;
	struct recursive_tree_node* left;
	struct recursive_tree_node* right;
	struct array_for_node nodearray;
}rectnode;



vector<tnode*> root_to_leaf_path_storage;
vector<tnode*> root_to_leaf_path_storage_for_testing;
vector<tnode*> leaf_storage_for_stash;
tnode *root =NULL;
rectnode *recroot =NULL;
vector<rectnode *>  root_of_recursive_trees;

//vector<tnode*> store_path(tnode *node,vector<tnode*>,int);
void store_path(tnode *node,vector<tnode*>,int);

// This is the new node function that that returns a new node of the basic tree type and the blocks that are supposed to be in the node are initialised as -1 initially...
tnode *newnode(int val){

	tnode *node = (tnode*)malloc(sizeof(tnode));
	node->data = val;
	node->left = NULL;
	node->right = NULL;
	node->arr[0]= -1 ;
	node->arr[1]= -1 ;
	node->arr[2]= -1 ;
	node->arr[3]= -1 ;

	return node;
}

// This is the new node function that that returns a new node of the recursive  tree type and the content of the block that stores the mapping of nodes to leaf nodes is initiallised to -1 above in the declaration for the array_for_node struct. This strucure has been designed as to change in the future if we are dealing with strings...
rectnode *recnewnode(int value){

	rectnode *node = (rectnode*)malloc(sizeof(rectnode));
	node->data = value;
	node->left = NULL;
	node->right = NULL;

	return node;

}

// This is the initial function that helps in creating the tree, in this case we are using the old age method of creating a BST when supplied with an array...
tnode *insert(int array[],int start, int end){


	if(start > end)
		return NULL;
	int mid = ( (end + start) /2);//  + start;
	tnode *root = newnode(array[mid]);
	/*
	int p =0;
	while ( p <= 3 ){
		root->arr[p] = data_value++;
		if (data_value   == numblocks)
			break;
		p++;
	}
	*/
	root->left = insert(array,start,mid-1);
	root->right = insert(array,mid+1,end);

	return root;

}

// This is preorder function that prints the basic tree in the preorder form....
void preOrder(tnode* node)
{
	if (node == NULL)
		return;

	cout<<"\t"<<node->data<<endl;
	cout<<"------- "<<node->arr[0]<<"------- "<<node->arr[1]<<"------- "<<node->arr[2]<<"------- "<<node->arr[3]<<"-----"<<endl;
	preOrder(node->left);
	preOrder(node->right);
}

// This is the function that finds out the leafs of the tree by supplying the root, this information about leafs is required as we will need it for mapping the nodes to leaf nodes of the tree. Here we are storing the information about the leafs in a unordered_map by the name of leaf_map that will contain all the leafs for the tree whose root has been passed..
void leaf_nodes(tnode *root){

	if (root == NULL)
		return;
	if (root->left == NULL && root->right == NULL){
		//cout<<"leaf = "<<root->data<<endl;
		if(leaf_map.find(root->data) ==  leaf_map.end())
			leaf_map.insert({leaf_count,root->data});
		leaf_count++;
	}
	leaf_nodes(root->left);
	leaf_nodes(root->right);

}

// This is the funstion which helps in storing the path from root to leaf in a vector which can be used for different manupulations. A global vector defined as root_to_leaf_path_storage will contain  the path from the root to the leaf having the value as value.
void path_from_root_to_leaf(tnode *root, int value){

	//cout<<"ROOT TO LEAF CALLED"<<endl;
	//cout<<"SIZE = "<<root_to_leaf_path_storage.size()<<endl;
	//for (int ii= 0 ;ii<root_to_leaf_path_storage.size() ; ii++)
	//	cout<<root_to_leaf_path_storage[ii]->data<<endl;
	store_path(root,root_to_leaf_path_storage,value);

}

// This is just continuation of the previous function and at the end of this the vector root_to_leaf_path_storage stores the path from root to leaf along with the content of the nodes... two addded vectors root_to_leaf_path_storage_for_testing and leaf_storage_for_stash are also there which are just copies of the maine vector,  they can be deleted if wanted to save the size of memory used.. these all three vector are global..
void store_path(tnode *node,vector<tnode*> root_to_leaf_path_storage,int value){

	if (node == NULL)
		//return  vector<tnode*>();
		return;

	//cout<<"ROOT TO LEAF SECOND PART  CALLED"<<endl;
	root_to_leaf_path_storage.push_back(node);
	//cout<<node->data<<endl;
	if (node->left == NULL && node->right == NULL){
		if (node->data == value){
			cout<<"root_to_leaf_path_storage now contains the desired path"<<endl;

			cout<<"Printing out the path from root to the leaf===================="<<endl;
			for(int n = 0 ; n < root_to_leaf_path_storage.size();n++)
				cout<<root_to_leaf_path_storage[n]->data<<endl;
			cout<<"GOt the pathhhhhhhhhhhhhhhhh now returning"<<endl;
			global_check = 1; // this is a global variable which is used just to check when path is found to return...
			root_to_leaf_path_storage_for_testing = root_to_leaf_path_storage;
			leaf_storage_for_stash = root_to_leaf_path_storage;
			return;
		}
		else{
			root_to_leaf_path_storage.clear();
		}
	}
	else{
		if (global_check == 1)
			return;
		store_path(node->left,root_to_leaf_path_storage,value);
		if (global_check == 1)
			return;
		store_path(node->right,root_to_leaf_path_storage,value);
		if (global_check == 1)
			return;

	}


}

// This is the basic LCA function which is used by
tnode* lowest_common_ancestor(tnode* node, int value1, int value2){

	// This is the iterative version ::  h being the height O(h) space is saved in this iterative version as no recursion stack is used.

	while (node != NULL){

		if (node->data < value1 && node->data < value2)
			node = node->right;
		else if (node->data > value1 && node->data > value2)
			node = node->left;
		else
			break;

	}

	cout<<"The LCA is node having value as  "<<node->data<<endl;
	return node;
	/*	

	// This is the recursive version

	if (node == NULL)
	return;
	if (node->data == value1 || node->data == value2 || node->data > value1 && node->data < value2){
	cout<<"The LCA is node having value as  "<<node->data<<endl;
	return;
	}
	if (node->data < value1 && node->data < value2)
	lowest_common_ancestor(node->right,value1,value2);
	if (node->data > value1 && node->data > value2)
	lowest_common_ancestor(node->left,value1,value2);
	 */
}

/* Filling the initial randomness of all the blocks in the position map  */

void remap_all_blocks(){

	srand (time(NULL)) ; //initialize the random seed
	for(int q = 0; q<numblocks; q++){
		if (position_map.find(q) == position_map.end())
			position_map.insert({q,leaf_map[rand() % leaf_count]});
	}

	cout<<"Printing the postition map"<<endl;
	cout<<"----------------------------------------------------------------------------------------------------------"<<endl;
	for (int q = 0 ; q < numblocks; q++)
		cout<<"  "<<q<<"     "<<position_map[q]<<endl;
	cout<<"----------------------------------------------------------------------------------------------------------"<<endl;

}


void proper_insert(tnode* node){
	
	cout<<"***********************************************************************************"<<endl;
	cout<<"***********************************************************************************"<<endl;
	cout<<"***********************************************************************************"<<endl;
	cout<<"***********************************************************************************"<<endl;
	for(int i =0 ; i < position_map.size(); i++)
		cout<<" "<<i<<"  "<<position_map[i]<<endl;

	


	int check = 0;
	for(int i =0 ; i < position_map.size(); i++){
		//cout<<"HAHAHAHAHAAHAHAHAHAHAHAHAHHHAHAHAHAHAHA"<<endl;
		global_check = 0;
		path_from_root_to_leaf(node,position_map[i]);
		check = 0;
		int stash_size = leaf_storage_for_stash.size();
		cout<<"for i = "<<i<<"  mapped to  = "<<position_map[i]<<endl;
		//for (int ii= 0 ;ii<stash_size ; ii++)
		//	cout<<leaf_storage_for_stash[ii]->data<<endl;
		while (stash_size != 0){
		if(leaf_storage_for_stash[stash_size - 1]->arr[0] == -1){
			leaf_storage_for_stash[stash_size - 1]->arr[0] = i;
			cout<<"Printing in = "<<leaf_storage_for_stash[stash_size - 1]->data<<"   arr[0]"<<endl;
			break;
			}
		else if(leaf_storage_for_stash[stash_size - 1]->arr[1] == -1){
			leaf_storage_for_stash[stash_size - 1]->arr[1] = i;
			cout<<"Printing in = "<<leaf_storage_for_stash[stash_size - 1]->data<<"    arr[1]"<<endl;
			break;
			}
		else if(leaf_storage_for_stash[stash_size - 1]->arr[2] == -1){
			leaf_storage_for_stash[stash_size - 1]->arr[2] = i;
			cout<<"Printing in = "<<leaf_storage_for_stash[stash_size - 1]->data<<"    arr[2]"<<endl;
			break;
			}
		else if(leaf_storage_for_stash[stash_size - 1]->arr[3] == -1){
			leaf_storage_for_stash[stash_size - 1]->arr[3] = i;
			cout<<"Printing in = "<<leaf_storage_for_stash[stash_size - 1]->data<<"     arr[3]"<<endl;
			break;
			}
		
		else
			stash_size -= 1;
			}
		if(stash_size == 0){
			cout<<"+++++++++++++++++++++++++Pushing in stash+++++++++++++++++++++++++++++++++++++"<<endl;
			stash.push_back({i,position_map[i]});
		}
	
	}
	

	cout<<"***********************************************************************************"<<endl;
	cout<<"***********************************************************************************"<<endl;
	cout<<"***********************************************************************************"<<endl;
	cout<<"***********************************************************************************"<<endl;
	
	for (int ii= 0 ;ii<stash.size() ; ii++)
		cout<<stash[ii].first<<"   "<<stash[ii].second<<endl;
	

}


void read(int block){
	
	cout<<"In READ Function"<<endl;

	global_check = 0;
	root_to_leaf_path_storage.clear();
	tnode* lca = NULL;
	int pos_value = 0;
	int leaf_value = -1;
	int new_leaf_value = -1;
	cout<<"In READ function"<<endl;
	srand(time(NULL));
	if (position_map.find(block) == position_map.end()){
		cout<<"block not in position map"<<endl;
	}
	else{
		cout<<" ==="<<block<<"==="<<position_map[block];
		leaf_value = position_map[block];
		position_map[block]=leaf_map[rand() % leaf_count];
		new_leaf_value = position_map[block];
	}

	cout<<"Printing the postition map"<<endl;
	cout<<"----------------------------------------------------------------------------------------------------------"<<endl;
	for (int q = 0 ; q < numblocks; q++)
		cout<<"  "<<q<<"     "<<position_map[q]<<endl;
	cout<<"----------------------------------------------------------------------------------------------------------"<<endl;

	path_from_root_to_leaf(root,leaf_value);
	cout<<"Old position leaf map  "<<leaf_value<<endl;
	cout<<"New position leaf map  "<<new_leaf_value<<endl;
	lca = lowest_common_ancestor(root,leaf_value,new_leaf_value);
	cout<<" Size = "<<root_to_leaf_path_storage_for_testing.size()<<endl;
	for(int n = 0 ; n < root_to_leaf_path_storage_for_testing.size();n++){
		cout<<root_to_leaf_path_storage_for_testing[n]->data<<endl;
		for(int kk = 0; kk < 4 ; kk++)
			cout<<root_to_leaf_path_storage_for_testing[n]->arr[kk]<<"\t";
		cout<<endl;
	}
	cout<<"=============================================="<<endl;	


	for(int n = 0 ; n < root_to_leaf_path_storage_for_testing.size();n++){
		//cout<<root_to_leaf_path_storage_for_testing[n]->data<<endl;
		for(int kk = 0; kk < 4 ; kk++){
			if(root_to_leaf_path_storage_for_testing[n]->arr[kk] != -1){
				stash.push_back({root_to_leaf_path_storage_for_testing[n]->arr[kk],position_map[root_to_leaf_path_storage_for_testing[n]->arr[kk]]});
				root_to_leaf_path_storage_for_testing[n]->arr[kk] = -1;
			}
		}
		cout<<endl;
	}


	cout<<"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"<<endl;
	cout<<"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"<<endl;
	cout<<"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"<<endl;
	cout<<stash.size()<<endl;
	for(int b=0 ; b<stash.size();b++)
		cout<<stash[b].first<<"   "<<stash[b].second<<endl;

	cout<<"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"<<endl;
	cout<<"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"<<endl;
	cout<<"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"<<endl;
	cout<<"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"<<endl;

	for(int k = 0 ; k < stash.size(); k++){
		int r = k + rand() % (stash.size() - k);
		swap(stash[k].first, stash[r].first);
		swap(stash[k].second, stash[r].second);
	}
	cout<<stash.size()<<endl;
	for(int b=0 ; b<stash.size();b++)
		cout<<stash[b].first<<"   "<<stash[b].second<<endl;


	for(int k = stash.size() -1 ; k >= 0; k-- ){
		int size_of_pathstorage = root_to_leaf_path_storage_for_testing.size();	
		lca = lowest_common_ancestor(root,root_to_leaf_path_storage_for_testing[size_of_pathstorage- 1]->data,stash[k].second);
		//cout<<lca->data<<endl;
		pos_value = 0;
		for(int n = 0 ; n < size_of_pathstorage ; n++){
			if(lca == root_to_leaf_path_storage_for_testing[n])
				pos_value = n + 1;
		}
		while(pos_value){
			if(root_to_leaf_path_storage_for_testing[pos_value - 1 ]->arr[0] == -1){
				root_to_leaf_path_storage_for_testing[pos_value - 1 ]->arr[0] = stash[k].first;
				stash.pop_back();
				break;

			}
			else if(root_to_leaf_path_storage_for_testing[pos_value - 1 ]->arr[1] == -1){
				root_to_leaf_path_storage_for_testing[pos_value - 1]->arr[1] = stash[k].first;
				stash.pop_back();
				break;
			}
			else if(root_to_leaf_path_storage_for_testing[pos_value - 1 ]->arr[2] == -1){
				root_to_leaf_path_storage_for_testing[pos_value - 1 ]->arr[2] = stash[k].first;
				stash.pop_back();
				break;
			}
			else if(root_to_leaf_path_storage_for_testing[pos_value - 1 ]->arr[3] == -1){
				root_to_leaf_path_storage_for_testing[pos_value - 1 ]->arr[3] = stash[k].first;
				stash.pop_back();
				break;
			}
			else
				pos_value -= 1; 
		}

	}


	cout<<stash.size()<<endl;
	for(int b=0 ; b<stash.size();b++)
		cout<<stash[b].first<<"   "<<stash[b].second<<endl;



	cout<<"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"<<endl;
	cout<<"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"<<endl;
	cout<<"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"<<endl;
	cout<<"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"<<endl;
	for(int n = 0 ; n < root_to_leaf_path_storage_for_testing.size();n++){
		cout<<root_to_leaf_path_storage_for_testing[n]->data<<endl;
		for(int kk = 0; kk < 4 ; kk++)
			cout<<root_to_leaf_path_storage_for_testing[n]->arr[kk]<<"\t";
		cout<<endl;
	}
	cout<<"=============================================="<<endl;	

	cout<<"Printing the tree"<<endl;
	preOrder(root);


}


void write(int block,int data){
	cout<<"In WRITE function"<<endl;

	global_check = 0;
	root_to_leaf_path_storage.clear();
	tnode* lca = NULL;
	int pos_value = 0;
	int leaf_value = -1;
	int new_leaf_value = -1;
	cout<<"In READ function"<<endl;
	srand(time(NULL));
	if (position_map.find(block) == position_map.end()){
		cout<<"block not in position map"<<endl;
	}
	else{
		cout<<" ==="<<block<<"==="<<position_map[block];
		leaf_value = position_map[block];
		position_map[block]=leaf_map[rand() % leaf_count];
		new_leaf_value = position_map[block];
	}

	cout<<"Printing the postition map"<<endl;
	cout<<"----------------------------------------------------------------------------------------------------------"<<endl;
	for (int q = 0 ; q < numblocks; q++)
		cout<<"  "<<q<<"     "<<position_map[q]<<endl;
	cout<<"----------------------------------------------------------------------------------------------------------"<<endl;

	path_from_root_to_leaf(root,leaf_value);
	cout<<"Old position leaf map  "<<leaf_value<<endl;
	cout<<"New position leaf map  "<<new_leaf_value<<endl;
	lca = lowest_common_ancestor(root,leaf_value,new_leaf_value);
	cout<<" Size = "<<root_to_leaf_path_storage_for_testing.size()<<endl;

	cout<<"BEFORE CHANGING DATA"<<endl;
	for(int n = 0 ; n < root_to_leaf_path_storage_for_testing.size();n++){
		cout<<root_to_leaf_path_storage_for_testing[n]->data<<endl;
		for(int kk = 0; kk < 4 ; kk++)
			cout<<root_to_leaf_path_storage_for_testing[n]->arr[kk]<<"\t";
		cout<<endl;
	}
	cout<<"=============================================="<<endl;	


	for(int n = 0 ; n < root_to_leaf_path_storage_for_testing.size();n++){
		cout<<root_to_leaf_path_storage_for_testing[n]->data<<endl;
		for(int kk = 0; kk < 4 ; kk++){
			if (root_to_leaf_path_storage_for_testing[n]->arr[kk] == block)
				root_to_leaf_path_storage_for_testing[n]->arr[kk] = data;	
			}
		cout<<endl;
	}
	cout<<"=============================================="<<endl;	
	cout<<"AFTER CHANGING DATA"<<endl;
	for(int n = 0 ; n < root_to_leaf_path_storage_for_testing.size();n++){
		cout<<root_to_leaf_path_storage_for_testing[n]->data<<endl;
		for(int kk = 0; kk < 4 ; kk++)
			cout<<root_to_leaf_path_storage_for_testing[n]->arr[kk]<<"\t";
		cout<<endl;
	}
	cout<<"=============================================="<<endl;	
	for(int n = 0 ; n < root_to_leaf_path_storage_for_testing.size();n++){
		//cout<<root_to_leaf_path_storage_for_testing[n]->data<<endl;
		for(int kk = 0; kk < 4 ; kk++){
			if(root_to_leaf_path_storage_for_testing[n]->arr[kk] != -1){
				stash.push_back({root_to_leaf_path_storage_for_testing[n]->arr[kk],position_map[root_to_leaf_path_storage_for_testing[n]->arr[kk]]});
				root_to_leaf_path_storage_for_testing[n]->arr[kk] = -1;
			}
		}
		cout<<endl;
	}


	cout<<"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"<<endl;
	cout<<"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"<<endl;
	cout<<"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"<<endl;
	cout<<stash.size()<<endl;
	for(int b=0 ; b<stash.size();b++)
		cout<<stash[b].first<<"   "<<stash[b].second<<endl;

	cout<<"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"<<endl;
	cout<<"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"<<endl;
	cout<<"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"<<endl;
	cout<<"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"<<endl;

	for(int k = 0 ; k < stash.size(); k++){
		int r = k + rand() % (stash.size() - k);
		swap(stash[k].first, stash[r].first);
		swap(stash[k].second, stash[r].second);
	}
	cout<<stash.size()<<endl;
	for(int b=0 ; b<stash.size();b++)
		cout<<stash[b].first<<"   "<<stash[b].second<<endl;


	for(int k = stash.size() -1 ; k >= 0; k-- ){
		int size_of_pathstorage = root_to_leaf_path_storage_for_testing.size();	
		lca = lowest_common_ancestor(root,root_to_leaf_path_storage_for_testing[size_of_pathstorage- 1]->data,stash[k].second);
		cout<<lca->data<<endl;
		pos_value = 0;
		for(int n = 0 ; n < size_of_pathstorage ; n++){
			if(lca == root_to_leaf_path_storage_for_testing[n])
				pos_value = n + 1;
		}
		cout<<"[[[[["<<pos_value<<"[[[["<<endl;
		while(pos_value){
			if(root_to_leaf_path_storage_for_testing[pos_value - 1 ]->arr[0] == -1){
				root_to_leaf_path_storage_for_testing[pos_value - 1 ]->arr[0] = stash[k].first;
				stash.pop_back();
				break;

			}
			else if(root_to_leaf_path_storage_for_testing[pos_value - 1 ]->arr[1] == -1){
				root_to_leaf_path_storage_for_testing[pos_value - 1]->arr[1] = stash[k].first;
				stash.pop_back();
				break;
			}
			else if(root_to_leaf_path_storage_for_testing[pos_value - 1 ]->arr[2] == -1){
				root_to_leaf_path_storage_for_testing[pos_value - 1 ]->arr[2] = stash[k].first;
				stash.pop_back();
				break;
			}
			else if(root_to_leaf_path_storage_for_testing[pos_value - 1 ]->arr[3] == -1){
				root_to_leaf_path_storage_for_testing[pos_value - 1 ]->arr[3] = stash[k].first;
				stash.pop_back();
				break;
			}
			else
				pos_value -= 1; 
		}

	}


	cout<<stash.size()<<endl;
	for(int b=0 ; b<stash.size();b++)
		cout<<stash[b].first<<"   "<<stash[b].second<<endl;



	cout<<"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"<<endl;
	cout<<"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"<<endl;
	cout<<"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"<<endl;
	cout<<"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"<<endl;
	for(int n = 0 ; n < root_to_leaf_path_storage_for_testing.size();n++){
		cout<<root_to_leaf_path_storage_for_testing[n]->data<<endl;
		for(int kk = 0; kk < 4 ; kk++)
			cout<<root_to_leaf_path_storage_for_testing[n]->arr[kk]<<"\t";
		cout<<endl;
	}
	cout<<"=============================================="<<endl;	

	cout<<"Printing the tree"<<endl;
	preOrder(root);


}

void recleaf_nodes(rectnode *root){

	if (root == NULL)
		return;
	if (root->left == NULL && root->right == NULL){
		cout<<"leaf = "<<root->data<<endl;
		if(recleaf_map.find(root->data) ==  recleaf_map.end())
			recleaf_map.insert({recleaf_count,root->data});
		recleaf_count++;
	}
	recleaf_nodes(root->left);
	recleaf_nodes(root->right);

}

rectnode *recinsert(int array[],int start, int end,int precount){


	if(start > end)
		return NULL;
	int mid = ( (end + start) /2);//  + start;
	rectnode *recroot = recnewnode(array[mid]);

	if (precount == -1){

		for (int i =0 ; i<4  ; i++){
			int j  = 0;
			recroot->nodearray.array_node[i][j] = mid*4 + i ;
			recroot->nodearray.array_node[i][j+1] = position_map[(mid*4 + i)];

		}
	}
	else{
		for (int i =0 ; i<4  ; i++){
			int j  = 0;
			recroot->nodearray.array_node[i][j] = mid*4 + i ;
			if(recposition_map[(mid*4 + i)] == 0)
				recroot->nodearray.array_node[i][j+1] = -1;
			else
				recroot->nodearray.array_node[i][j+1] = recposition_map[(mid*4 + i)];
		}

	}


	if (precount == -1){
		recroot->left = recinsert(array,start,mid-1,-1);
		recroot->right = recinsert(array,mid+1,end,-1);
	}
	else{
		recroot->left = recinsert(array,start,mid-1,0);
		recroot->right = recinsert(array,mid+1,end,0);

	}
	return recroot;

}

void recpreOrder(rectnode* node)
{
	if (node == NULL)
		return;

	cout<<"\t"<<node->data<<endl;
	for (int i =0; i <4 ;i++){
		int j =0;
		cout<<"\t    "<<node->nodearray.array_node[i][j]<<"    "<< node->nodearray.array_node[i][j+1]<<endl;
	}
	recpreOrder(node->left);
	recpreOrder(node->right);
}
void normalpreOrder(rectnode* node)
{
	if (node == NULL)
		return;

	cout<<"\t"<<node->data<<endl;
	/*for (int i =0; i <4 ;i++){
	  int j =0;
	  cout<<"\t    "<<node->nodearray.array_node[i][j]<<"    "<< node->nodearray.array_node[i][j+1]<<endl;
	  }*/
	recpreOrder(node->left);
	recpreOrder(node->right);
}

//int count = 0;
void recursive_tree(rectnode* node, int map_size){

	
	root_of_recursive_trees.push_back(node);	

	if(node == NULL || node->left == NULL && node->right == NULL)
		return;
	//root_of_recursive_trees.push_back(node);	

	cout<<"+++++++++++++++++RECURSIVE  TREEEEEEEEE STARTS ++++++++++++++++++++++++++++++"<<endl;

	cout<<"++++++++++++++++++++++++++++++++++++++++++++++++++"<<endl;
	cout<<node->data<<endl;
	cout<<"++++++++++++++++++++++++++++++++++++++++++++++++++"<<endl;
	cout<<recposition_map.size()<<endl;
	count++;

	//normalpreOrder(node);
	recleaf_count = 0;
	recleaf_nodes(node);
	cout<<recleaf_map.size()<<endl;
	cout<<"Printing the leaf nodes : "<<endl;
	for (int i =0 ; i < recleaf_map.size() ; i++)
		cout<<"i = "<<i<<"  "<<recleaf_map[i]<<endl;


	if (count == 1){

		srand (time(NULL)) ; //initialize the random seed
		for(int q = 0; q < int(ceil(float(position_map.size())/4)); q++){
			if (recposition_map.find(q) == recposition_map.end())
				recposition_map.insert({q,recleaf_map[rand() % recleaf_map.size()]});
		}
	}
	else{
		srand (time(NULL)) ; //initialize the random seed
		for(int q = 0; q < int(ceil(float(map_size)/4)); q++){
			if (recposition_map.find(q) == recposition_map.end())
				recposition_map.insert({q,recleaf_map[rand() % recleaf_map.size()]});
		}


	}
	cout<<"New position map reduced size"<<endl;
	for (int q = 0 ; q < recposition_map.size(); q++)
		cout<<"  "<<q<<"     "<<recposition_map[q]<<endl;



	int rec_arr[int(ceil(float(recposition_map.size())/4))];


	cout<<int(ceil(float(recposition_map.size())/4))<<endl;
	for(int i = 0; i < int(ceil(float(recposition_map.size())/4)); i++)
		rec_arr[i] = i+1 ;

	recroot = recinsert(rec_arr,0,ceil(float(recposition_map.size())/4) -1,0);
	cout<<"Printing the recursive tree in Preorder "<<endl;
	recpreOrder(recroot);
	cout<<"++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"<<endl;
	cout<<"++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"<<endl;
	int tmp = recposition_map.size();
	recposition_map.clear();
	recleaf_map.clear();
	recursive_tree(recroot,tmp);	
}


//int main(int argc,char *argv[])
int main()
{

	int height,output;

	cout<<"Enter the number of blocks"<<endl;
	cin>>numblocks;

	int number_of_nodes_in_tree,number_of_leaf_nodes,nodes_in_com_bal_bt,diff_nodes;
	number_of_nodes_in_tree = int(ceil(float(numblocks)/4));
	height = int(ceil(float(log2 (number_of_nodes_in_tree + 1)) - 1));
	nodes_in_com_bal_bt = pow(2,height+1) -1;
	diff_nodes = nodes_in_com_bal_bt - number_of_nodes_in_tree;
	number_of_leaf_nodes = pow(2,height) - diff_nodes;


	cout<<"Number of nodes required = "<<number_of_nodes_in_tree<<endl;
	cout<<"Number of leaf nodes = "<<number_of_leaf_nodes<<endl;

	int number_arr[number_of_nodes_in_tree];
	for(int i =0; i < number_of_nodes_in_tree;i++)
		number_arr[i] = i+1;

	root = insert(number_arr,0,number_of_nodes_in_tree-1);
	cout<<"Printing the tree in Preorder "<<endl;
	
	preOrder(root);
	printf("\n");
	leaf_nodes(root);
	cout<<"Printing the leaf_map containing all the leaf nodes "<<endl;
	cout<<"Leaf-number "<<"Node-value "<<endl;
	for(int k = 0 ; k < leaf_count;k++)
		cout<<"    "<<k+1<<"          " <<leaf_map[k]<<endl;

	remap_all_blocks();
	
	proper_insert(root);
	preOrder(root);
	//int node_val = 7;
	//path_from_root_to_leaf(root,node_val);

	//lowest_common_ancestor(root,val1,val2);
	//lowest_common_ancestor(root,2,6);


	int option,blllll,daaattt,wrrrrr;
	cout<<"Choose one of the the oprions"<<endl;
	cout<<"1. Read"<<endl;
	cout<<"2. Write"<<endl;
	cout<<"3. Exit"<<endl;
	cin>>option;

	switch(option){

		case 1:
			cout<<"Enter the block to be read"<<endl;
			cin>>blllll;
			if (blllll < 0 || blllll > numblocks -1 ){
				cout<<"Enter a proper value"<<endl;
				exit(EXIT_FAILURE);
				}
			read(blllll);
			break;
		case 2:
			cout<<"Enter the block to be written"<<endl;
			cin>>daaattt;
			cout<<"Now the data to be written"<<endl;
			cin>>wrrrrr;
			if (wrrrrr < 0 || wrrrrr > numblocks -1 ){
				cout<<"Enter a proper value"<<endl;
				exit(EXIT_FAILURE);
				}
			write(daaattt,wrrrrr);
			break;
		case 3:
			exit(EXIT_FAILURE);
			break;
		default:
			cout<<"No option matches, try again"<<endl;
			exit(EXIT_FAILURE);
			break;
	}

	cout<<"=============================RECURSIVE PART STARTS ==========================================================="<<endl;

	int rec_tree_arr[int(ceil(float(position_map.size())/4))];
	for(int i = 0; i < int(ceil(float(position_map.size())/4)); i++)
		rec_tree_arr[i] = i+1 ;

	recroot = recinsert(rec_tree_arr,0,int(ceil(float(position_map.size())/4)) -1, -1);
	cout<<"Printing the recursive tree in Preorder "<<endl;
	recpreOrder(recroot);
	int length = 0 ;
	recursive_tree(recroot,length);
	

	cout<<"Printing the address for  all the roots of all the recursive trees"<<endl;
	cout<<root<<endl;
	for (int l =0 ; l <root_of_recursive_trees.size() ; l++)
		cout<<root_of_recursive_trees[l]<<endl;


	return 0;
}


