Halloween
=========

A trick or treat plugin

Making money from this is a big no no.
Same for claiming it as yours, server unique and the like.

Shame asking you to have the text 'Made for RedWarfare' in the greeting message is unreasonable.


As for creating the mysql table.

CREATE TABLE `Halloween` (
  `uuid` varchar(40) NOT NULL,
  `Costume` varchar(30) NOT NULL DEFAULT 'NO_DISGUISE',
  `Exp` float NOT NULL DEFAULT '0',
  `Looted` varchar(4000) NOT NULL DEFAULT '',
  `Potions` varchar(200) NOT NULL DEFAULT '',
  `TimesLooted` int(6) NOT NULL DEFAULT '0',
  `Tricks` int(6) NOT NULL DEFAULT '0',
  `Treats` int(6) NOT NULL DEFAULT '0',
  `Repeats` int(6) NOT NULL DEFAULT '0',
  UNIQUE KEY `uuid` (`uuid`)
)