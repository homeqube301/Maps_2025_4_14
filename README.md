# ArchiveMaps 
#### 「座標も画像も、地球をメモ帳へ」、同期可能高機能メモアプリです。

# 目次
- [ダウンロード](#ダウンロード)
- [アプリの主機能](#アプリ主機能)
- [技術スタック](#技術スタック)

# ダウンロード
DeployGateから配布しております  
https://dply.me/tex2rn

# アプリ主機能
* GoogleMap上へマーカーを設置
  * マーカー一つ一つにはメモや画像、動画などを保存可能      


  
* 表示
  * Google Maps APIを使用した地図表示
  * 現在位置の表示
  * 作成したマーカーの一覧表示
  * 視覚的に理解しやすいアイコンを用いたボタン表示
  * Nominatim APIを用いたマーカーの住所表示    


* データ
  * 設置したマーカー情報をSQLデータベースで保存（アカウント同期が必要）
  * SQLデータベースにはそれぞれ「マーカー名」「メモ」「設置日時」「座標」「マーカーカラー」を主に保存
  * アカウントを用いないゲストモードでもローカルストレージ保存を用いて利用可能
 
* 検索機能
  * フィルタリング機能搭載
  * フィルタリング機能は主に「マーカー名」「メモ内容（完全一致検索）」「設置日時」で構成
  * Embeddings APIを用いたメモ内容の意味検索（アカウント同期が必要）



* アカウント同期
  * Supabase Authを使用した認証機能
  * メールアドレスとパスワードによるログイン
  * アカウント連携により別の端末でもメモを自由に管理
  * SQLデータベースに保存されたメモ内容から意味検索が可能

* オンボーディング機能
    * アプリ開始時、各操作スクリーンにオンボーディングを用いた視覚的にわかりやすい操作説明


# 技術スタック
* Jetpack ライブラリ
  * Navigation
  * ViewModel
  * DataStore
 
* 権限
  * Accompanist

* Dependency Injection
  * Dagger Hilt

* 非同期処理
  * Kotlin Coroutines

* 通信
 * Moshi
 * Retrofit2
 * Okhttp3

## 連携API
 * [Maps SDK for Android](https://developers.google.com/maps/documentation/android-sdk/overview?hl=ja)  
   地図を表示する際に使用
 * [Nominatim](https://nominatim.org/)  
   マーカー情報の住所表示に使用
 * [Supabase](https://supabase.com/docs/guides/api)  
   Authを用いたアカウント認証やデータベースへマーカー情報の保存をする際に使用  
 * [Vector embeddings](https://platform.openai.com/docs/guides/embeddings)  
   検索ワードと保存されたメモ内容をベクトル化し、ユークリッド距離を計算するために使用。

   
   









