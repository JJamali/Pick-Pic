# Generated by Django 4.2.18 on 2025-03-08 00:19

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0002_eventuser_accepted_image_score'),
    ]

    operations = [
        migrations.AddField(
            model_name='event',
            name='last_modified',
            field=models.DateTimeField(auto_now=True),
        ),
    ]
