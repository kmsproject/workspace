# workspace

## git操作手順  
1. ローカルリポジトリ作成  
	`git clone https://github.com/kmsproject/workspace.git`  
1. ローカルブランチ作成  
	`git checkout -b [ブランチ名]`  
1. ファイルの編集  
	ファイルを作成・変更する  
1. コミットを作成  
	`git add .`  
	`git commit -m "[ここにコミットコメント]"`   
1. ローカルブランチをリモートブランチに反映  
	`git push origin [ブランチ名]`  
1. 開発ブランチの変更をリモートのmasterに反映  
	`git checkout master`  
	`git merge [マージしたいブランチ名]`  
	`git push master`  
  
## git設定関連  
- Untracked files の追加方法  
	`git ls-files --others --exclude-standard | xargs git add`  
- git config でユーザー名とメールアドレスを設定する  
	`git config --global user.name [githubの表示名]`  
	`git config --global user.email [githubに設定したメールアドレス]`  
- git push 時のパスワードをキャッシュに保存する  
	`git config --global credential.helper 'cache --timeout=1209600'`  
