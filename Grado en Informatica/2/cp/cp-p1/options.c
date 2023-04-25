#include <getopt.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include "options.h"

static struct option long_options[] = {
	{ .name = "threads",
	  .has_arg = required_argument,
	  .flag = NULL,
	  .val = 't'},
	{ .name = "buffer_size",
	  .has_arg = required_argument,
	  .flag = NULL,
	  .val = 'b'},
	{ .name = "iterations",
	  .has_arg = required_argument,
	  .flag = NULL,
	  .val = 'i'},
	{ .name = "delay",
	  .has_arg = required_argument,
	  .flag = NULL,
	  .val = 'd'},
	{ .name = "help",
	  .has_arg = no_argument,
	  .flag = NULL,
	  .val = 'h'},
	{0, 0, 0, 0}
};

static void usage(int i)
{
	printf(
		"Usage:  swap [OPTION]\n"
		"Options:\n"
		"  -t n, --threads=<n>: number of threads\n"
		"  -b n, --buffer_size=<n>: size of buffer\n"
		"  -i n, --iterations=<n>: total number of iterations\n"
		"  -d n, --delay=<n>: delay between buffer ops (us)\n"
		"  -h, --help: this message\n\n"
	);
	exit(i);
}

static int get_int(char *arg, int *value)
{
	char *end;
	*value = strtol(arg, &end, 10);

	return (end != NULL);
}

int handle_options(int argc, char **argv, struct options *opt)
{
	while (1) {
		int c;
		int option_index = 0;

		c = getopt_long (argc, argv, "ht:b:i:d:",
				 long_options, &option_index);
		if (c == -1)
			break;

		switch (c) {
		case 't':
			if (!get_int(optarg, &opt->num_threads)
			    || opt->num_threads <= 0) {
				printf("'%s': is not a valid integer\n",
				       optarg);
				usage(-3);
			}
			break;

		case 'b':
			if (!get_int(optarg, &opt->buffer_size)
			    || opt->buffer_size <= 0) {
				printf("'%s': is not a valid integer\n",
				       optarg);
				usage(-3);
			}
			break;

		case 'i':
			if (!get_int(optarg, &opt->iterations)
			    || opt->iterations <= 0) {
				printf("'%s': is not a valid integer\n",
				       optarg);
				usage(-3);
			}
			break;

		case 'd':
			if (!get_int(optarg, &opt->delay)
			    || opt->delay <= 0) {
				printf("'%s': is not a valid integer\n",
				       optarg);
				usage(-3);
			}
			break;


		case '?':
		case 'h':
			usage(0);
			break;

		default:
			printf ("?? getopt returned character code 0%o ??\n", c);
			usage(-1);
		}
	}
	return 0;
}

int read_options(int argc, char **argv, struct options *opt) {

	int result = handle_options(argc,argv,opt);
	
	if (result != 0)
		exit(result);

	if (argc - optind != 0) {
		printf ("Too many arguments\n\n");
		while (optind < argc)
			printf ("'%s' ", argv[optind++]);
		printf ("\n");
		usage(-2);
	}
	
	return 0;
}
